package com.kepler.management.invoker.impl;

import java.util.concurrent.ThreadPoolExecutor;

import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;
import com.kepler.management.Period;
import com.kepler.management.invoker.Feeder;
import com.kepler.management.invoker.Invokers;

/**
 * @author kim 2015年7月22日
 */
public class InvokerPeriod extends Period {

	/**
	 * 默认120秒, 最小60秒
	 */
	private final static int INTERVAL = Math.max(60000, Integer.valueOf(PropertiesUtils.get(InvokerPeriod.class.getName().toLowerCase() + ".interval", "120000")));

	private final Invokers invokers;

	private final Feeder feeder;

	private final Host local;

	public InvokerPeriod(ThreadPoolExecutor threads, Invokers invokers, Feeder feeder, Host local) {
		super(threads);
		this.invokers = invokers;
		this.feeder = feeder;
		this.local = local;
	}

	@Override
	protected long interval() {
		return InvokerPeriod.INTERVAL;
	}

	@Override
	protected void trigger() {
		this.feeder.feed(this.local, this.invokers.hosts());
	}
}
