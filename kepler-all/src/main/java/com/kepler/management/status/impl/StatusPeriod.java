package com.kepler.management.status.impl;

import java.util.concurrent.ThreadPoolExecutor;

import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.management.Period;
import com.kepler.management.status.Feeder;
import com.kepler.management.status.Status;

/**
 * @author kim 2015年7月22日
 */
public class StatusPeriod extends Period {

	/**
	 * 默认120秒, 最小60秒
	 */
	private final static int INTERVAL = Math.max(60000, Integer.valueOf(PropertiesUtils.get(StatusPeriod.class.getName().toLowerCase() + ".interval", "120000")));

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
	protected long interval() {
		return StatusPeriod.INTERVAL;
	}

	@Override
	protected void trigger() {
		this.feeder.feed(this.local, this.status.get());
	}
}
