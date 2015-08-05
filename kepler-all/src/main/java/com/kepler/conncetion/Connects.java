package com.kepler.conncetion;

import com.kepler.host.Host;

/**
 * @author kim 2015年7月10日
 */
public interface Connects {

	public Host get() throws Exception;

	public void put(Host host);
}
