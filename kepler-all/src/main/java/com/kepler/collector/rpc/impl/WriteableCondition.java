package com.kepler.collector.rpc.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.kepler.ack.Status;
import com.kepler.collector.rpc.Condition;
import com.kepler.host.Host;

/**
 * @author kim 2015年7月24日
 */
public class WriteableCondition implements Condition {

	private static final long serialVersionUID = 1L;

	private final AtomicLong rtt = new AtomicLong();

	private final AtomicLong total = new AtomicLong();

	private final AtomicLong timeout = new AtomicLong();

	private final AtomicLong exception = new AtomicLong();

	private final Host source;

	private final Host target;
	
	public WriteableCondition(Host source, Host target) {
		super();
		this.source = source;
		this.target = target;
	}

	@Override
	public Host source() {
		return this.source;
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

	public WriteableCondition touch() {
		this.total.incrementAndGet();
		return this;
	}

	public WriteableCondition rtt(long rtt) {
		this.rtt.addAndGet(rtt);
		return this;
	}

	public WriteableCondition timeout(Status status) {
		if (status.equals(Status.TIMEOUT)) {
			this.timeout.incrementAndGet();
		}
		return this;
	}

	public WriteableCondition exception(Status status) {
		if (status.equals(Status.EXCEPTION)) {
			this.exception.incrementAndGet();
		}
		return this;
	}
	
	public void clear() {
		this.rtt.set(0);
		this.total.set(0);
		this.timeout.set(0);
		this.exception.set(0);
	}
}