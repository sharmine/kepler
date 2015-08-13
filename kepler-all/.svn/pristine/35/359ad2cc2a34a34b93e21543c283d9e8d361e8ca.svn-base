package com.kepler.management.transfer.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.ack.Status;
import com.kepler.config.PropertiesUtils;
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

	private final static int FREEZE = Integer.valueOf(PropertiesUtils.get(DefaultTransfers.class.getName().toLowerCase() + ".freeze", "5"));

	private final Collection<Transfer> removed = new HashSet<Transfer>();

	private final String uuid = UUID.randomUUID().toString();

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
	public Collection<Transfer> transfers() {
		return this.transfers.values();
	}

	public void clear() {
		for (Object condition : this.transfers.values()) {
			this.clear(WriteableTransfer.class.cast(condition));
		}
		this.trim();
	}

	/**
	 * Remove or clean
	 * 
	 * @param transfer
	 */
	private void clear(WriteableTransfer transfer) {
		if (transfer.freezed()) {
			this.removed.add(transfer);
		} else {
			transfer.reset();
		}
	}

	private void trim() {
		Iterator<Transfer> removed = this.removed.iterator();
		while (removed.hasNext()) {
			this.remove(removed.next());
			removed.remove();
		}
	}

	private void remove(Transfer each) {
		Transfer removed = Transfer.class.cast(this.transfers.remove(each.local(), each.target()));
		if (removed != null) {
			DefaultTransfers.LOGGER.warn("Transfer: (" + removed.local().getAsString() + " (" + removed.local().group() + ") to " + removed.target().getAsString() + " (" + removed.target().group() + ") removed ... (" + this + ")");
		}
	}

	// 并发情况下出现允许范围内的丢失 (进出现在首次初始化时)
	public void put(Host local, Host target, Status status, long rtt) {
		WriteableTransfer transfer = WriteableTransfer.class.cast(this.transfers.get(local, target));
		this.transfers.put(local, target, (transfer = (transfer != null ? transfer : new WriteableTransfer(local, target))).touch().rtt(rtt).timeout(status).exception(status));
	}

	private class WriteableTransfer implements Transfer {

		private final static long serialVersionUID = 1L;

		private final AtomicLong rtt = new AtomicLong();

		private final AtomicLong total = new AtomicLong();

		private final AtomicLong freeze = new AtomicLong();

		private final AtomicLong timeout = new AtomicLong();

		private final AtomicLong exception = new AtomicLong();

		private final Host local;

		private final Host target;

		public WriteableTransfer(Host local, Host target) {
			super();
			this.local = local;
			this.target = target;
			DefaultTransfers.LOGGER.warn("WriteableTransfer (" + DefaultTransfers.this.uuid + ") created: " + local.getAsString() + " (" + local.group() + " / " + target.getAsString() + " (" + target.group() + ") for (" + DefaultTransfers.this.service() + " / " + DefaultTransfers.this.version() + ")");
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

		public void reset() {
			this.rtt.set(0);
			this.total.set(0);
			this.timeout.set(0);
			this.exception.set(0);
		}

		public boolean freezed() {
			return this.total.get() == 0 ? this.freeze.incrementAndGet() > DefaultTransfers.FREEZE : this.warm();
		}

		private boolean warm() {
			this.freeze.set(0);
			return false;
		}
	}
}