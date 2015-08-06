package com.kepler.collector.rpc.impl;

/**
 * @author kim 2015年8月6日
 */
public class GroupHost {

	private final String group;

	private final String host;

	public GroupHost(String group, String host) {
		super();
		this.group = group;
		this.host = host;
	}

	public String group() {
		return this.group;
	}

	public String host() {
		return this.host;
	}
}
