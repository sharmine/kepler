package com.kepler.channel.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kepler.channel.ChannelContext;
import com.kepler.channel.ChannelInvoker;
import com.kepler.host.Host;
import com.kepler.host.HostLocks;
import com.kepler.host.HostsContext;
import com.kepler.host.impl.SegmentLocks;

/**
 * 主机 - 通道映射
 * 
 * @author kim 2015年7月9日
 */
public class DefaultContext implements ChannelContext {

	/**
	 * Using ConcurrentHashMap for this.channels.keySet()
	 */
	private final Map<Host, ChannelInvoker> channels = new ConcurrentHashMap<Host, ChannelInvoker>();

	private final HostLocks lock = new SegmentLocks();

	private final HostsContext context;

	public DefaultContext(HostsContext context) {
		super();
		this.context = context;
	}

	public ChannelInvoker get(Host host) {
		return this.channels.get(host);
	}

	public ChannelInvoker del(Host host) {
		synchronized (this.lock.get(host)) {
			return this.channels.remove(host);
		}
	}

	public ChannelInvoker put(Host host, ChannelInvoker invoker) {
		synchronized (this.lock.get(host)) {
			this.channels.put(host, invoker);
			this.context.unban(host);
		}
		return invoker;
	}

	public boolean contain(Host host) {
		return this.channels.containsKey(host);
	}
}
