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
	 * 默认60秒, 最小30秒
	 */
	private final static int INTERVAL = Math.max(30000, Integer.valueOf(PropertiesUtils.get(StatusPeriod.class.getName().toLowerCase() + ".interval", "60000")));

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
	// 如果存在Exported则激活Status监控
	public void exported(Class<?> service, String version, Object instance) throws Exception {
		if (this.activate.compareAndSet(false, true)) {
			super.init();
		}
	}

	@Override
	protected long interval() {
		return StatusPeriod.INTERVAL;
	}

	@Override
	protected void doing() {
		StatusPeriod.this.feeder.feed(this.local, StatusPeriod.this.status.get());
	}
}
