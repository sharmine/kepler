package com.kepler.ack.impl;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月10日
 */
public class AckTimer implements Runnable {

	/**
	 * 超时监听线程
	 */
	private final static int THREADS = Integer.valueOf(PropertiesUtils.get(AckTimer.class.getName().toLowerCase() + ".executor", "2"));

	private final static Log LOGGER = LogFactory.getLog(AckTimer.class);

	private final AtomicBoolean shutdown = new AtomicBoolean();

	private final ThreadPoolExecutor threads;

	private final Acks acks;

	public AckTimer(Acks acks, ThreadPoolExecutor threads) {
		super();
		this.acks = acks;
		this.threads = threads;
	}

	public void init() {
		for (int index = 0; index < AckTimer.THREADS; index++) {
			this.threads.execute(this);
		}
	}

	public void destory() {
		this.shutdown.set(true);
	}

	public void run() {
		while (!this.shutdown.get()) {
			try {
				// 被ThreadPoolExecutor终端关闭
				this.acks.timeout().cancel();
			} catch (Throwable e) {
				e.printStackTrace();
				AckTimer.LOGGER.error(e.getMessage(), e);
			}
		}
		AckTimer.LOGGER.info(this.getClass() + " shutdown ...");
	}
}