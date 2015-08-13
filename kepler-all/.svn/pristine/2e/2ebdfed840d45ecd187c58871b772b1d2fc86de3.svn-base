package com.kepler.router.routing;

import java.util.concurrent.atomic.AtomicInteger;

import com.kepler.host.Host;
import com.kepler.host.HostCollector;
import com.kepler.protocol.Request;
import com.kepler.router.Routing;

/**
 * @author kim 2015年7月14日
 */
public class RoundRobin implements Routing {

	public final static String NAME = "round-robin";

	private final AtomicInteger indexes = new AtomicInteger();

	@Override
	public Host route(Request request, HostCollector collect) {
		return collect.all().get((this.indexes.incrementAndGet() & Byte.MAX_VALUE) % collect.all().size());
	}

	public String name() {
		return RoundRobin.NAME;
	}
}
