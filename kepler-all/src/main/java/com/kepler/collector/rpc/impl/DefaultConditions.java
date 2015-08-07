package com.kepler.collector.rpc.impl;

import java.util.Collection;

import com.kepler.ack.Status;
import com.kepler.collector.rpc.Condition;
import com.kepler.collector.rpc.Conditions;
import com.kepler.host.Host;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author kim 2015年7月24日
 */
public class DefaultConditions implements Conditions {

	private static final long serialVersionUID = 1L;

	private final MultiKeyMap conditions = new MultiKeyMap();

	private final String service;

	private final String version;

	private final String method;

	public DefaultConditions(String service, String version, String method) {
		super();
		this.service = service;
		this.version = version;
		this.method = method;
	}

	public String service() {
		return this.service;
	}

	public String version() {
		return this.version;
	}

	public String method() {
		return this.method;
	}

	@SuppressWarnings("unchecked")
	public Collection<Condition> conditions() {
		return this.conditions.values();
	}

	public void clear() {
		for (Object condition : this.conditions.values()) {
			Condition.class.cast(condition).clear();
		}
	}

	// 并发情况下, 首次初始化会存在丢失
	public void put(Host local, Host host, Status status, long rtt) {
		WriteableCondition condition = WriteableCondition.class.cast(this.conditions.get(local, host));
		this.conditions.put(local, host, (condition = (condition != null ? condition : new WriteableCondition(local, host))).touch().rtt(rtt).timeout(status).exception(status));
	}
}