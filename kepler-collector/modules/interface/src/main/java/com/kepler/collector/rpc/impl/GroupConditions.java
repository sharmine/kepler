package com.kepler.collector.rpc.impl;

import java.util.Collection;

import com.kepler.collector.rpc.Condition;
import com.kepler.collector.rpc.Conditions;
import com.kepler.host.Host;
import com.kepler.host.impl.DefaultHost;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author kim 2015年8月6日
 */
public class GroupConditions implements Conditions {

	private static final long serialVersionUID = 1L;

	private final MultiKeyMap conditions = new MultiKeyMap();

	private final String service;

	private final String version;

	private final String method;

	private final int base;

	public GroupConditions(int base, String service, String version, String method) {
		super();
		this.service = service;
		this.version = version;
		this.method = method;
		this.base = base;
	}

	@Override
	public String service() {
		return this.service;
	}

	public String getService() {
		return this.service();
	}

	@Override
	public String version() {
		return this.version;
	}

	public String getVersion() {
		return this.version();
	}

	@Override
	public String method() {
		return this.method;
	}

	public String getMethod() {
		return this.method();
	}

	public GroupConditions put(Group local, Group host, long rtt, long total, long timeout, long exception) {
		AvgCondition condition = AvgCondition.class.cast(this.conditions.get(local, host));
		this.conditions.put(local, host, (condition = (condition != null ? condition : new AvgCondition().put(local, host, rtt, total, timeout, exception))));
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Condition> conditions() {
		return this.conditions.values();
	}

	public Collection<Condition> getConditions() {
		return this.conditions();
	}

	private class AvgCondition implements Condition {

		private static final long serialVersionUID = 1L;

		private long rtt;

		private long total;

		private long timeout;

		private long exception;

		private Group local;

		private Group host;

		public AvgCondition put(Group local, Group host, long rtt, long total, long timeout, long exception) {
			this.rtt += rtt;
			this.total += total;
			this.timeout += timeout;
			this.exception += exception;
			// No check
			this.local = local;
			this.host = host;
			return this;
		}

		@Override
		public Host local() {
			return DefaultHost.valueOf(this.local.getHost());
		}

		@SuppressWarnings("unused")
		public Group getLocal() {
			return this.local;
		}

		@Override
		public Host host() {
			return DefaultHost.valueOf(this.host.getHost());
		}

		@SuppressWarnings("unused")
		public Group getGroup() {
			return this.host;
		}

		@Override
		public long rtt() {
			return this.rtt / GroupConditions.this.base;
		}

		@SuppressWarnings("unused")
		public long getRtt() {
			return this.rtt();
		}

		@Override
		public long total() {
			return this.total / GroupConditions.this.base;
		}

		@SuppressWarnings("unused")
		public long getTotal() {
			return this.total();
		}

		@Override
		public long timeout() {
			return this.timeout / GroupConditions.this.base;
		}

		@SuppressWarnings("unused")
		public long getTimeout() {
			return this.timeout();
		}

		@Override
		public long exception() {
			return this.exception / GroupConditions.this.base;
		}

		@SuppressWarnings("unused")
		public long getException() {
			return this.exception();
		}

		@Override
		public void clear() {
		}
	}
}