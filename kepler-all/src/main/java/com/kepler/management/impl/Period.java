package com.kepler.management.impl;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author kim 2015年7月22日
 */
abstract public class Period implements Runnable {

	private final static Log LOGGER = LogFactory.getLog(Period.class);

	/**
	 * 当前周期
	 */
	private final AtomicLong period = new AtomicLong(this.next());

	private final AtomicBoolean shutdown = new AtomicBoolean();

	private final ThreadPoolExecutor threads;

	public Period(ThreadPoolExecutor threads) {
		super();
		this.threads = threads;
	}

	/**
	 * 实际操作
	 */
	abstract protected void doing();

	abstract protected long interval();

	/**
	 * 下个周期
	 * 
	 * @return
	 */
	private long next() {
		return System.currentTimeMillis() + this.interval();
	}

	public void init() {
		this.threads.execute(this);
	}

	public void destory() {
		this.shutdown.set(true);
	}

	@Override
	public void run() {
		while (!this.shutdown.get()) {
			try {
				this.waiting();
				Period.LOGGER.debug("Waiting next period ... (" + this.getClass() + ")");
			} catch (Throwable e) {
				e.printStackTrace();
				Period.LOGGER.error(e.getMessage(), e);
			} finally {
				this.period.set(this.next());
			}
		}
		Period.LOGGER.warn(this.getClass() + " shutdown ... (" + this.getClass() + ")");
	}

	private void waiting() throws InterruptedException {
		while (System.currentTimeMillis() < this.period.get()) {
			synchronized (this) {
				this.wait(Period.this.interval() / 3);
			}
		}
		this.doing();
	}
}
