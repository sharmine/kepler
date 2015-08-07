package com.kepler.collector.rpc.impl;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import com.kepler.ack.Status;
import com.kepler.collector.rpc.Note;
import com.kepler.collector.rpc.Notes;
import com.kepler.host.Host;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author kim 2015年7月24日
 */
public class DefaultNotes implements Notes {

	private static final long serialVersionUID = 1L;

	private final MultiKeyMap notes = new MultiKeyMap();

	private final String service;

	private final String version;

	private final String method;

	public DefaultNotes(String service, String version, String method) {
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
	public Collection<Note> notes() {
		return this.notes.values();
	}

	public void clear() {
		for (Object condition : this.notes.values()) {
			Note.class.cast(condition).reset();
		}
	}

	// 并发情况下, 首次初始化会存在丢失
	public void put(Host local, Host host, Status status, long rtt) {
		WriteableNote note = WriteableNote.class.cast(this.notes.get(local, host));
		this.notes.put(local, host, (note = (note != null ? note : new WriteableNote(local, host))).touch().rtt(rtt).timeout(status).exception(status));
	}

	private class WriteableNote implements Note {

		private static final long serialVersionUID = 1L;

		private final AtomicLong rtt = new AtomicLong();

		private final AtomicLong total = new AtomicLong();

		private final AtomicLong timeout = new AtomicLong();

		private final AtomicLong exception = new AtomicLong();

		private final Host local;

		private final Host target;

		public WriteableNote(Host local, Host target) {
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

		public WriteableNote touch() {
			this.total.incrementAndGet();
			return this;
		}

		public WriteableNote rtt(long rtt) {
			this.rtt.addAndGet(rtt);
			return this;
		}

		public WriteableNote timeout(Status status) {
			if (status.equals(Status.TIMEOUT)) {
				this.timeout.incrementAndGet();
			}
			return this;
		}

		public WriteableNote exception(Status status) {
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
	}
}