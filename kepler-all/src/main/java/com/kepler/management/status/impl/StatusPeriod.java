package com.kepler.management.status.impl;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.management.impl.Period;
import com.kepler.management.status.Feeder;
import com.kepler.management.status.Status;
import com.kepler.service.Exported;

/**
 * @author kim 2015年7月22日
 */
public class StatusPeriod extends Period implements Exported {

	/**
	 * 默认120秒, 最小60秒
	 */
	private final static int INTERVAL = Math.max(60000, Integer.valueOf(PropertiesUtils.get(StatusPeriod.class.getName().toLowerCase() + ".interval", "120000")));

	private final AtomicBoolean activate = new AtomicBoolean();

	private final Status status;

	private final Feeder feeder;

	private final Host local;

	public StatusPeriod(Host local, Feeder feeder, Status status, ThreadPoolExecutor threads) {
		super(threads);
		this.local = local;
		this.feeder = feeder;
		this.status = status;
	}

	@Override
	public void exported(Class<?> service, String version, Object instance) throws Exception {
		this.activate.set(true);
	}

	@Override
	public long interval() {
		return StatusPeriod.INTERVAL;
	}

	@Override
	public void doPeriod() {
		if (this.activate.get()) {
			StatusPeriod.this.feeder.feed(this.local, StatusPeriod.this.status.get());
		}
	}
}
