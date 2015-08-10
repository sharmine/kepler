package com.kepler.management.search.impl;

import java.io.Serializable;

/**
 * @author kim 2015年8月6日
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String group;

	private final String host;

	public Group(String group, String host) {
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

	public int hashCode() {
		return this.group.hashCode() ^ this.getHost().hashCode();
	}

	public boolean equals(Object ob) {
		Group group = Group.class.cast(ob);
		return this.getGroup().equals(group.getGroup()) && this.getHost().equals(group.getHost());
	}
}
