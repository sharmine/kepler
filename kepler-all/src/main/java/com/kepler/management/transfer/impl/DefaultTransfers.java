package com.kepler.management.transfer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.ack.Status;
import com.kepler.host.Host;
import com.kepler.management.transfer.Transfer;
import com.kepler.management.transfer.Transfers;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author kim 2015年7月24日
 */
public class DefaultTransfers implements Transfers {

	private final static long serialVersionUID = 1L;

	private final static Log LOGGER = LogFactory.getLog(DefaultTransfers.class);

	private final Collection<Transfer> removed = new ArrayList<Transfer>();

	private final MultiKeyMap transfers = new MultiKeyMap();

	private final String service;

	private final String version;

	private final String method;

	public DefaultTransfers(String service, String version, String method) {
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
	public Collection<Transfer> transfer() {
		return this.transfers.values();
	}

	public void clear() {
		for (Object condition : this.transfers.values()) {
			this.clear(Transfer.class.cast(condition));
		}
		this.remove();
	}

	/**
	 * Remove or clean
	 * 
	 * @param transfer
	 */
	private void clear(Transfer transfer) {
		if (transfer.pause()) {
			this.removed.add(transfer);
		} else {
			transfer.reset();
		}
	}

	private void remove() {
		Iterator<Transfer> removed = this.removed.iterator();
		while (removed.hasNext()) {
			this.remove(removed.next());
			removed.remove();
		}
	}

	private void remove(Transfer each) {
		if (this.transfers.remove(each.local(), each.target()) != null) {
			DefaultTransfers.LOGGER.warn("Transfer: (" + each.local().getAsString() + " to " + each.target().getAsString() + ") removed ...");
		}
	}

	// 并发情况下, 首次初始化会存在丢失
	public void put(Host local, Host target, Status status, long rtt) {
		WriteableTransfer transfer = WriteableTransfer.class.cast(this.transfers.get(local, target));
		this.transfers.put(local, target, (transfer = (transfer != null ? transfer : new WriteableTransfer(local, target))).touch().rtt(rtt).timeout(status).exception(status));
	}

	private class WriteableTransfer implements Transfer {

		private final static long serialVersionUID = 1L;

		private final AtomicLong rtt = new AtomicLong();

		private final AtomicLong total = new AtomicLong();

		private final AtomicLong timeout = new AtomicLong();

		private final AtomicLong exception = new AtomicLong();

		private final Host local;

		private final Host target;

		public WriteableTransfer(Host local, Host target) {
			super();
			this.local = local;
			this.target = target;
		}

		@Override
		public Host local() {
			return this.local;
		}

		@Override
		public Host target() {
			return this.target;
		}

		public long rtt() {
			return this.rtt.get();
		}

		public long total() {
			return this.total.get();
		}

		public long timeout() {
			return this.timeout.get();
		}

		public long exception() {
			return this.exception.get();
		}

		public WriteableTransfer touch() {
			this.total.incrementAndGet();
			return this;
		}

		public WriteableTransfer rtt(long rtt) {
			this.rtt.addAndGet(rtt);
			return this;
		}

		public WriteableTransfer timeout(Status status) {
			if (status.equals(Status.TIMEOUT)) {
				this.timeout.incrementAndGet();
			}
			return this;
		}

		public WriteableTransfer exception(Status status) {
			if (status.equals(Status.EXCEPTION)) {
				this.exception.incrementAndGet();
			}
			return this;
		}

		public boolean pause() {
			return this.total.get() == 0;
		}

		public void reset() {
			this.rtt.set(0);
			this.total.set(0);
			this.timeout.set(0);
			this.exception.set(0);
		}
	}
}