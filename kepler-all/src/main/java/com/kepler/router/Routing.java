package com.kepler.router;

import com.kepler.host.Host;
import com.kepler.host.HostCollector;
import com.kepler.protocol.Request;

/**
 * @author kim 2015年7月14日
 */
public interface Routing {

	public Host route(Request request, HostCollector collect);

	public String name();
}
