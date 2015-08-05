package com.kepler.host.impl;

import java.io.Serializable;

import com.kepler.host.Host;

/**
 * @author kim 2015年7月8日
 */
public class DefaultHost implements Host, Serializable {

	private final static long serialVersionUID = 1L;

	private final int port;

	private final String tag;

	private final String host;

	public DefaultHost(String tag, String host, int port) {
		super();
		this.host = host;
		this.port = port;
		this.tag = tag;
	}

	@Override
	public String host() {
		return this.host;
	}

	@Override
	public String tag() {
		return this.tag;
	}

	@Override
	public int port() {
		return this.port;
	}

	public int hashCode() {
		return this.host().hashCode() ^ this.port();
	}

	public boolean equals(Object ob) {
		Host host = Host.class.cast(ob);
		return this.host().equals(host.host()) && (this.port() == host.port());
	}

	public String getAsString() {
		return this.toString();
	}

	public boolean loop(Host host) {
		return this.host().equalsIgnoreCase(host.host());
	}

	public String toString() {
		return this.host + ":" + this.port;
	}

	public static DefaultHost valueOf(String strings) {
		String[] param = strings.split(":");
		return new DefaultHost(Host.TAG_DEFAULT, param[0], Integer.valueOf(param[1]));
	}
}