package com.kepler.collector.rpc.impl;

import java.util.Collection;

import com.kepler.collector.rpc.Note;
import com.kepler.collector.rpc.Notes;
import com.kepler.host.Host;
import com.kepler.host.impl.DefaultHost;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;

/**
 * @author kim 2015年8月6日
 */
public class GroupNotes implements Notes {

	private static final long serialVersionUID = 1L;

	private final MultiKeyMap notes = new MultiKeyMap();

	private final String service;

	private final String version;

	private final String method;

	private final int base;

	public GroupNotes(int base, String service, String version, String method) {
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
	public Collection<Note> notes() {
		return this.notes.values();
	}

	public GroupNotes put(Group local, Group target, long rtt, long total, long timeout, long exception) {
		AvgNote note = AvgNote.class.cast(this.notes.get(local, target));
		this.notes.put(local, target, (note = (note != null ? note : new AvgNote().put(local, target, rtt, total, timeout, exception))));
		return this;
	}

	public Collection<Note> getNotes() {
		return this.notes();
	}

	private class AvgNote implements Note {

		private static final long serialVersionUID = 1L;

		private long rtt;

		private long total;

		private long timeout;

		private long exception;

		private Group local;

		private Group target;

		public AvgNote put(Group target, Group host, long rtt, long total, long timeout, long exception) {
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
			return this.rtt / GroupNotes.this.base;
		}

		@SuppressWarnings("unused")
		public long getRtt() {
			return this.rtt();
		}

		@Override
		public long total() {
			return this.total / GroupNotes.this.base;
		}

		@SuppressWarnings("unused")
		public long getTotal() {
			return this.total();
		}

		@Override
		public long timeout() {
			return this.timeout / GroupNotes.this.base;
		}

		@SuppressWarnings("unused")
		public long getTimeout() {
			return this.timeout();
		}

		@Override
		public long exception() {
			return this.exception / GroupNotes.this.base;
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