package com.kepler.router.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.kepler.router.RouteChange;
import com.kepler.router.RouteChanges;

/**
 * @author kim 2015年7月10日
 */
public class DefaultChanges implements RouteChanges {

	private final BlockingQueue<RouteChange> queue = new LinkedBlockingQueue<RouteChange>();

	@Override
	public void put(RouteChange change) {
		this.queue.offer(change);
	}

	@Override
	public RouteChange get() throws Exception {
		return this.queue.take();
	}
}
