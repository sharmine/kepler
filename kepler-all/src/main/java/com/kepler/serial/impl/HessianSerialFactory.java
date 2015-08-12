package com.kepler.serial.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.kepler.config.PropertiesUtils;
import com.kepler.serial.SerialFactory;

/**
 * @author kim 2015年7月8日
 */
public class HessianSerialFactory implements SerialFactory {

	private final static int BUFFER = Integer.valueOf(PropertiesUtils.get(HessianSerialFactory.class.getName().toLowerCase() + ".buffer", "1024"));

	public byte[] serial(Object data) throws IOException {
		try (AutoCloseOutput output = new AutoCloseOutput()) {
			return output.writeObject(data).toByteArray();
		}
	}

	public <T> T serial(byte[] data, Class<T> clazz) throws IOException {
		return this.serial(data, 0, data.length, clazz);
	}

	public <T> T serial(byte[] data, int offest, Class<T> clazz) throws IOException {
		return this.serial(data, offest, data.length, clazz);
	}

	public <T> T serial(byte[] data, int offset, int length, Class<T> clazz) throws IOException {
		try (AutoCloseInput input = new AutoCloseInput(data, offset, length)) {
			return clazz.cast(input.readObject());
		}
	}

	private final class AutoCloseInput implements Closeable {

		private final HessianInput input;

		public AutoCloseInput(byte[] arrays, int offset, int length) {
			this.input = new HessianInput(new BufferedInputStream(new ByteArrayInputStream(arrays, offset, length)));
		}

		public Object readObject() throws IOException {
			return this.input.readObject();
		}

		@Override
		public void close() throws IOException {
			// Hessian会级联关闭
			this.input.close();
		}
	}

	private final class AutoCloseOutput implements Closeable {

		private final ByteArrayOutputStream arrays = new ByteArrayOutputStream(HessianSerialFactory.BUFFER);

		private HessianOutput output = new HessianOutput(new BufferedOutputStream(this.arrays));

		public AutoCloseOutput writeObject(Object ob) throws IOException {
			this.output.writeObject(ob);
			this.output.flush();
			return this;
		}

		public byte[] toByteArray() {
			return this.arrays.toByteArray();
		}

		@Override
		public void close() throws IOException {
			// Output不会级联关闭
			this.arrays.close();
			this.output.close();
		}
	}
}
