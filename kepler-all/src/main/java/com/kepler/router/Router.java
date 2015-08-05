package com.kepler.router;

import com.kepler.host.Host;
import com.kepler.protocol.Request;

/**
 * @author kim 2015年7月8日
 */
public interface Router {

	public Host route(Request request);
}
