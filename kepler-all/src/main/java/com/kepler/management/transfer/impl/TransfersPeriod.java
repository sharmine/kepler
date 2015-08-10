package com.kepler.management.transfer.impl;

import java.util.concurrent.ThreadPoolExecutor;

import com.kepler.config.PropertiesUtils;
import com.kepler.management.impl.Period;
import com.kepler.management.transfer.Collector;
import com.kepler.management.transfer.Feeder;

/**
 * @author kim 2015年7月22日
 */
public class TransfersPeriod extends Period {

	/**
	 * 默认30秒, 最小15秒
	 */
	private final static int INTERVAL = Math.max(15000, Integer.valueOf(PropertiesUtils.get(TransfersPeriod.class.getName().toLowerCase() + ".interval", "30000")));

	private final Collector collector;

	private final Feeder feeder;

	public TransfersPeriod(Feeder feeder, Collector collector, ThreadPoolExecutor threads) {
		super(threads);
		this.feeder = feeder;
		this.collector = collector;
	}

	@Override
	protected long interval() {
		return TransfersPeriod.INTERVAL;
	}

	@Override
	protected void doing() {
		TransfersPeriod.this.feeder.feed(TransfersPeriod.this.collector.transfers());
	}
}
