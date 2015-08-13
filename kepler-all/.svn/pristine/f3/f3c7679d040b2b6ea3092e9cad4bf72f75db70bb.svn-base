package com.kepler.zookeeper;

import java.util.concurrent.atomic.AtomicBoolean;

import com.kepler.KeplerException;
import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月10日
 */
public class ZkConnection {

	private final static int TIMEOUT = Integer.valueOf(PropertiesUtils.get(ZkConnection.class.getName().toLowerCase() + ".timeout", "15000"));

	private final AtomicBoolean valid = new AtomicBoolean();

	private final long start = System.currentTimeMillis();

	public void await() throws Exception {
		if (!this.valid.get()) {
			this.doWait();
		}
	}

	public void activate() {
		synchronized (this) {
			this.valid.set(true);
			this.notifyAll();
		}
	}

	private void doWait() throws Exception {
		while (!this.valid.get()) {
			synchronized (this) {
				this.wait(ZkConnection.TIMEOUT / 3);
			}
			this.timeout();
		}
	}

	private void timeout() {
		if ((System.currentTimeMillis() - this.start) > ZkConnection.TIMEOUT) {
			throw new KeplerException("ZooKeeper connect timeout ...");
		}
	}
}
