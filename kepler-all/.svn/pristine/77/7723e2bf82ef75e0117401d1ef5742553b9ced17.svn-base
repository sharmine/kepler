package com.kepler.connection.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.kepler.config.PropertiesUtils;
import com.kepler.conncetion.ConnectHost;
import com.kepler.conncetion.Connects;
import com.kepler.host.Host;

/**
 * 延迟重连
 * 
 * @author kim 2015年7月10日
 */
public class DefaultConnects implements Connects {

	private final static int DELAY = Integer.valueOf(PropertiesUtils.get(DefaultConnects.class.getName().toLowerCase() + ".delay", "5000"));

	private final BlockingQueue<ConnectHost> queue = new DelayQueue<ConnectHost>();

	@Override
	public Host get() throws Exception {
		return this.queue.take().host();
	}

	@Override
	public void put(Host host) {
		this.queue.offer(new DefaultConnectHost(host));
	}

	private class DefaultConnectHost implements ConnectHost {

		/**
		 * 租期(当前时间 + delay时间)
		 */
		private final long deadline = TimeUnit.MILLISECONDS.convert(DefaultConnects.DELAY, TimeUnit.MILLISECONDS) + System.currentTimeMillis();

		private final Host host;

		public DefaultConnectHost(Host host) {
			super();
			this.host = host;
		}

		public long getDelay(TimeUnit unit) {
			return unit.convert(this.deadline - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}

		public int compareTo(Delayed o) {
			return o.getDelay(TimeUnit.SECONDS) >= this.getDelay(TimeUnit.SECONDS) ? 1 : -1;
		}

		@Override
		public Host host() {
			return this.host;
		}
	}
}
