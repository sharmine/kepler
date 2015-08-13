package com.kepler.connection.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.channel.ChannelContext;
import com.kepler.channel.ChannelInvoker;
import com.kepler.conncetion.Closeable;
import com.kepler.host.Host;
import com.kepler.host.HostsContext;

/**
 * @author kim 2015年7月11日
 */
public class DefaultCloseable implements Closeable {

	private final static Log LOGGER = LogFactory.getLog(DefaultCloseable.class);

	private final HostsContext hosts;

	private final ChannelContext channels;

	public DefaultCloseable(HostsContext hosts, ChannelContext channels) {
		super();
		this.hosts = hosts;
		this.channels = channels;
	}

	@Override
	public void close(Host host) throws Exception {
		// 如果多个请求(Request)同时出现故障并关闭将使Invoker返回Null
		ChannelInvoker invoker = this.channels.del(host);
		if (invoker != null) {
			this.hosts.ban(host);
			invoker.close();
			DefaultCloseable.LOGGER.warn("Host:" + host.getAsString() + " closed and attempt reconnect ...");
		}
	}
}
