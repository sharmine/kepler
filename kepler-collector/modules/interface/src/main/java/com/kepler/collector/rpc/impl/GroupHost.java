package com.kepler.collector.rpc.impl;

import java.io.Serializable;

/**
 * @author kim 2015年8月6日
 */
public class GroupHost implements Serializable{

	private static final long serialVersionUID = 1L;

	private final String group;

	private final String host;

	public GroupHost(String group, String host) {
		super();
		this.group = group;
		this.host = host;
	}

	public String getGroup() {
		return this.group;
	}

	public String getHost() {
		return this.host;
	}
}
