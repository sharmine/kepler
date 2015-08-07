package com.kepler.ack.impl;

import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.KeplerException;
import com.kepler.ack.Ack;
import com.kepler.ack.Cancel;
import com.kepler.ack.Status;
import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.protocol.Request;
import com.kepler.protocol.Response;

/**
 * @author kim 2015年7月23日
 */
public class AckFuture implements Future<Object>, Ack, Cancel {

	/**
	 * 最大超时
	 */
	private final static int TIMEOUT = Integer.valueOf(PropertiesUtils.get(AckFuture.class.getName().toLowerCase() + ".timeout", "15000"));

	private final static Log LOGGER = LogFactory.getLog(AckFuture.class);

	/**
	 * 超时队列过期时间(当前时间 + delay时间)
	 */
	private final long deadline = TimeUnit.MILLISECONDS.convert(AckFuture.TIMEOUT, TimeUnit.MILLISECONDS) + System.currentTimeMillis();

	/**
	 * ACK创建时间
	 */
	private final long start = System.currentTimeMillis();

	/**
	 * 等待ACK线程
	 */
	private final Thread thread = Thread.currentThread();

	private final Request request;

	private final Host local;

	private final Host host;

	private Status stauts = Status.WAITING;

	/**
	 * 服务响应
	 */
	private Response response;

	/**
	 * 是否已超时(内部While Condition)
	 */
	private boolean timeout;

	/**
	 * 是否已取消(外部调用Cancel)
	 */
	private boolean canceled;

	public AckFuture(Host local, Host host, Request request) {
		super();
		this.host = host;
		this.local = local;
		this.request = request;
	}

	/**
	 * 是否超时
	 * 
	 * @return
	 */
	private AckFuture checkTimeout() throws KeplerException {
		// 外部取消表示为超时
		if (this.canceled || this.timeout) {
			this.stauts = Status.TIMEOUT;
			throw new KeplerException("ACK: " + this.request.ack() + " for (" + this.request.service() + " / " + this.request.version() + ") to " + this.host.getAsString() + " timeout after: " + this.elapse());
		}
		return this;
	}

	/**
	 * 服务抛出异常(远程服务异常)
	 * 
	 * @return
	 */
	private AckFuture checkException() throws KeplerException {
		if (!this.response.valid()) {
			this.stauts = Status.EXCEPTION;
			throw new KeplerException(this.response.throwable());
		}
		return this;
	}

	public Host host() {
		return this.host;
	}

	public Host local() {
		return this.local;
	}

	public Status status() {
		return this.stauts;
	}

	public Request request() {
		return this.request;
	}

	/**
	 * Response callback
	 * 
	 * @param
	 */
	public void response(Response response) {
		this.response = response;
		synchronized (this) {
			this.stauts = Status.DONE;
			this.notifyAll();
		}
	}

	@Override
	public boolean cancel() {
		return this.cancel(true);
	}

	/**
	 * mayInterruptIfRunning always = true
	 * 
	 * @return
	 */
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (!this.canceled) {
			AckFuture.LOGGER.warn("ACK: " + this.request.ack() + " (Using" + this.elapse() + ") will be canceled ...");
			this.canceled = true;
			this.thread.interrupt();
		}
		return this.canceled;
	}

	@Override
	public boolean isCancelled() {
		return this.canceled;
	}

	@Override
	public boolean isDone() {
		return Status.DONE.equals(this.stauts);
	}

	@Override
	// 使用默认超时
	public Object get() {
		return this.get(AckFuture.TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public Object get(long timeout, TimeUnit unit) {
		// 取Timeout最小值
		long lease = Math.min(timeout, AckFuture.TIMEOUT);
		while (this.continued() && !this.timeout(lease)) {
			try {
				synchronized (this) {
					this.wait(lease / 3);
				}
			} catch (Throwable e) {
				// 中断并捕获,由while condition确认是否继续等待
				e.printStackTrace();
				AckFuture.LOGGER.debug(e.getMessage(), e);
			}
		}
		return this.checkTimeout().checkException().response.response();
	}

	/**
	 * Response尚未回调并且等待尚未被外部取消
	 * 
	 * @return
	 */
	private boolean continued() {
		return !this.isDone() && !this.isCancelled();
	}

	/**
	 * 当前已消耗时间大于租期
	 * 
	 * @param lease
	 * @return
	 */
	private boolean timeout(long lease) {
		return (this.timeout = (this.elapse() > lease));
	}

	/**
	 * 当前已消耗时间
	 * 
	 * @return
	 */
	public long elapse() {
		return System.currentTimeMillis() - this.start;
	}

	public long getDelay(TimeUnit unit) {
		return unit.convert(this.deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public int compareTo(Delayed o) {
		return o.getDelay(TimeUnit.SECONDS) >= this.getDelay(TimeUnit.SECONDS) ? 1 : -1;
	}
}