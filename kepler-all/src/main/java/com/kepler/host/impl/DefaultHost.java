package com.kepler.host.impl;

import java.net.InetSocketAddress;

import com.kepler.host.Host;

/**
 * @author kim 2015年7月8日
 */
public class DefaultHost implements Host {

	private final static long serialVersionUID = 1L;

	private final int port;

	private final String pid;

	private final String tag;

	private final String host;

	private final String group;

	public DefaultHost(InetSocketAddress address, String pid) {
		this(Host.GROUP, Host.TAG, pid, address.getHostName(), address.getPort());
	}

	public DefaultHost(String group, String tag, String pid, String host, int port) {
		super();
		this.group = group;
		this.host = host;
		this.port = port;
		this.tag = tag;
		this.pid = pid;
	}

	@Override
	public String group() {
		return this.group;
	}

	@Override
	public String host() {
		return this.host;
	}

	@Override
	public String tag() {
		return this.tag;
	}

	public String pid() {
		return this.pid;
	}

	@Override
	public int port() {
		return this.port;
	}

	public int hashCode() {
		return this.host().hashCode() ^ this.port();
	}

	public boolean equals(Object ob) {
		// Not null point security
		Host host = Host.class.cast(ob);
		return this.host().equals(host.host()) && (this.port() == host.port());
	}

	public String getAsString() {
		return this.toString();
	}

	public boolean loop(Host host) {
		return this.host().equalsIgnoreCase(host.host());
	}

	public boolean loop(String host) {
		return this.host().equalsIgnoreCase(host);
	}

	public String toString() {
		return this.host + ":" + this.port;
	}

	public static DefaultHost valueOf(String strings, String group, String tag, String pid) {
		String[] param = strings.split(":");
		return new DefaultHost(group, tag, pid, param[0], param.length == 2 ? Integer.valueOf(param[1]) : -1);
	}
}