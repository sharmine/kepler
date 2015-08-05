package com.kepler.collector.rpc.impl;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.collector.Note;
import com.kepler.collector.rpc.Collector;
import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月22日
 */
public class Period implements Runnable {

	/**
	 * 默认30秒, 最小15秒
	 */
	private final static int PERIOD = Math.max(15000, Integer.valueOf(PropertiesUtils.get(Period.class.getName().toLowerCase() + ".period", "30000")));

	private final static Log LOGGER = LogFactory.getLog(Period.class);

	private final AtomicBoolean shutdown = new AtomicBoolean();

	private final ThreadPoolExecutor threads;

	private final Collector collector;

	private final Note note;

	/**
	 * 当前周期
	 */
	private long period = this.next();

	public Period(Note note, Collector collector, ThreadPoolExecutor threads) {
		super();
		this.note = note;
		this.threads = threads;
		this.collector = collector;
	}

	/**
	 * 下个周期
	 * 
	 * @return
	 */
	private long next() {
		return System.currentTimeMillis() + Period.PERIOD;
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
				Period.LOGGER.debug("Period reported ... ");
			} catch (Throwable e) {
				e.printStackTrace();
				Period.LOGGER.error(e.getMessage(), e);
			} finally {
				this.period = this.next();
			}
		}
		Period.LOGGER.warn(this.getClass() + " shutdown ...");
	}

	private void waiting() throws InterruptedException {
		while (System.currentTimeMillis() < this.period) {
			synchronized (this) {
				this.wait(Period.PERIOD / 3);
			}
		}
		Period.this.note.condition(Period.this.collector.conditions());
	}
}
