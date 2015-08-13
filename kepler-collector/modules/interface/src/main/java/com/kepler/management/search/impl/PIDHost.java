package com.kepler.management.search.impl;

import com.kepler.host.Host;

/**
 * @author kim 2015年8月13日
 */
public class PIDHost implements Host, Comparable<Host> {

	private static final long serialVersionUID = 1L;

	private final Host host;

	public PIDHost(Host host) {
		super();
		this.host = host;
	}

	@Override
	public int port() {
		return this.host.port();
	}

	@Override
	public String pid() {
		return this.host.pid();
	}

	public String getPid() {
		return this.pid();
	}

	@Override
	public String tag() {
		return this.host.tag();
	}

	public String getTag() {
		return this.tag();
	}

	@Override
	public String host() {
		return this.host.host();
	}

	public String getHost() {
		return this.host();
	}

	@Override
	public String group() {
		return this.host.group();
	}

	public String getGroup() {
		return this.group();
	}

	@Override
	public String getAsString() {
		return this.host.getAsString();
	}

	@Override
	public boolean loop(Host host) {
		return this.host.loop(host);
	}

	@Override
	public boolean loop(String host) {
		return this.host.loop(host);
	}

	@Override
	public int compareTo(Host host) {
		return String.CASE_INSENSITIVE_ORDER.compare(new StringBuffer().append(this.group()).append(this.getAsString()).append(this.pid()).toString(), new StringBuffer().append(host.group()).append(host.getAsString()).append(host.pid()).toString());
	}
}
