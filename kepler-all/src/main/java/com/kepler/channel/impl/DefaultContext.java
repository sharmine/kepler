package com.kepler.channel.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.channel.ChannelContext;
import com.kepler.channel.ChannelInvoker;
import com.kepler.host.Host;
import com.kepler.host.HostsContext;

/**
 * 主机 - 通道映射
 * 
 * @author kim 2015年7月9日
 */
public class DefaultContext implements ChannelContext {

	private final static Log LOGGER = LogFactory.getLog(DefaultContext.class);

	/**
	 * Using ConcurrentHashMap for this.channels.keySet()
	 */
	private final Map<Host, ChannelInvoker> channels = new ConcurrentHashMap<Host, ChannelInvoker>();

	private final HostsContext context;

	public DefaultContext(HostsContext context) {
		super();
		this.context = context;
	}

	public ChannelInvoker get(Host host) {
		return this.channels.get(host);
	}

	public ChannelInvoker del(Host host) {
		DefaultContext.LOGGER.warn("Host:" + host.getAsString() + " removed");
		return this.channels.remove(host);
	}

	public ChannelInvoker put(Host host, ChannelInvoker invoker) {
		DefaultContext.LOGGER.warn("Host:" + host.getAsString() + " added");
		this.channels.put(host, invoker);
		this.context.unban(host);
		return invoker;
	}

	public Collection<Host> hosts() {
		return this.channels.keySet();
	}

	public boolean contain(Host host) {
		return this.channels.containsKey(host);
	}
}
