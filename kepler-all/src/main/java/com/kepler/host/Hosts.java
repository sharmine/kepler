package com.kepler.host;

import com.kepler.protocol.Request;

/**
 * @author kim 2015年7月9日
 */
public interface Hosts {

	public void put(Host host);

	public void ban(Host host);

	public void unban(Host host);

	/**
	 * 是否含指定Host(含Ban列表)
	 * 
	 * @param host
	 * @return
	 */
	public boolean contain(Host host);

	public Host next(Request request);
}
