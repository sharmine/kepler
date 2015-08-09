package com.kepler.management.transfer.impl;

import java.util.Collection;

import com.kepler.host.Host;
import com.kepler.host.impl.DefaultHost;
import com.kepler.management.transfer.Transfer;
import com.kepler.management.transfer.Transfers;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author kim 2015年8月6日
 */
public class GroupTransfers implements Transfers {

	private static final long serialVersionUID = 1L;

	private final MultiKeyMap transfers = new MultiKeyMap();

	private final String service;

	private final String version;

	private final String method;

	private final int base;

	public GroupTransfers(int base, String service, String version, String method) {
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

	@Override
	@SuppressWarnings("unchecked")
	public Collection<Transfer> transfer() {
		return this.transfers.values();
	}

	public GroupTransfers put(Group local, Group target, long rtt, long total, long timeout, long exception) {
		AvgTransfer transfer = AvgTransfer.class.cast(this.transfers.get(local, target));
		this.transfers.put(local, target, (transfer = (transfer != null ? transfer : new AvgTransfer().put(local, target, rtt, total, timeout, exception))));
		return this;
	}

	public Collection<Transfer> getTransfers() {
		return this.transfer();
	}

	private class AvgTransfer implements Transfer {

		private static final long serialVersionUID = 1L;

		private long rtt;

		private long total;

		private long timeout;

		private long exception;

		private Group local;

		private Group target;

		public AvgTransfer put(Group target, Group host, long rtt, long total, long timeout, long exception) {
			this.rtt += rtt;
			this.total += total;
			this.timeout += timeout;
			this.exception += exception;
			// No check
			this.local = target;
			this.target = host;
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
		public Host target() {
			return DefaultHost.valueOf(this.target.getHost());
		}

		@SuppressWarnings("unused")
		public Group getGroup() {
			return this.target;
		}

		@Override
		public long rtt() {
			return this.rtt / GroupTransfers.this.base;
		}

		@SuppressWarnings("unused")
		public long getRtt() {
			return this.rtt();
		}

		@Override
		public long total() {
			return this.total / GroupTransfers.this.base;
		}

		@SuppressWarnings("unused")
		public long getTotal() {
			return this.total();
		}

		@Override
		public long timeout() {
			return this.timeout / GroupTransfers.this.base;
		}

		@SuppressWarnings("unused")
		public long getTimeout() {
			return this.timeout();
		}

		@Override
		public long exception() {
			return this.exception / GroupTransfers.this.base;
		}

		@SuppressWarnings("unused")
		public long getException() {
			return this.exception();
		}

		@Override
		public void reset() {
		}
	}
}