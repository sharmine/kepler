package com.kepler.channel;

import java.util.Collection;

import com.kepler.host.Host;

/**
 * @author kim 2015年7月9日
 */
public interface ChannelContext {

	public ChannelInvoker get(Host host);

	public ChannelInvoker del(Host host);

	/**
	 * Put and unban
	 * 
	 * @param host
	 * @param invoker
	 * @return
	 */
	public ChannelInvoker put(Host host, ChannelInvoker invoker);

	public Collection<Host> hosts();

	public boolean contain(Host host);
}
