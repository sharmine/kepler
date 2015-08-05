package com.kepler.serial;

import java.io.IOException;

/**
 * @author kim 2015年7月8日
 */
public interface SerialFactory {

	public byte[] serial(Object data) throws IOException;

	public <T> T serial(byte[] data, Class<T> clazz) throws IOException;

	public <T> T serial(byte[] data, int offest, Class<T> clazz) throws IOException;

	public <T> T serial(byte[] data, int offest, int length, Class<T> clazz) throws IOException;
}
