package com.kepler.collector.rpc.impl;

import com.kepler.collector.rpc.Condition;
import com.kepler.host.Host;
import com.kepler.host.impl.DefaultHost;

/**
 * 均值
 * 
 * @author kim 2015年7月27日
 */
public class GroupCondition implements Condition {

	private static final long serialVersionUID = 1L;

	private int index;

	private long rtt;

	private long total;

	private long timeout;

	private long exception;

	private GroupHost source;

	private GroupHost target;

	public GroupCondition put(GroupHost source, GroupHost target, long rtt, long total, long timeout, long exception) {
		this.index++;
		this.rtt += rtt;
		this.total += total;
		this.timeout += timeout;
		this.exception += exception;
		// No check
		this.source = source;
		this.target = target;
		return this;
	}

	@Override
	public Host source() {
		return DefaultHost.valueOf(this.source.getHost());
	}

	public GroupHost getSource() {
		return this.source;
	}

	@Override
	public Host target() {
		return DefaultHost.valueOf(this.target.getHost());
	}

	public GroupHost getTarget() {
		return this.target;
	}

	@Override
	public long rtt() {
		return this.rtt / this.index;
	}

	public long getRtt() {
		return this.rtt();
	}

	@Override
	public long total() {
		return this.total / this.index;
	}

	public long getTotal() {
		return this.total();
	}

	@Override
	public long timeout() {
		return this.timeout / this.index;
	}

	public long getTimeout() {
		return this.timeout();
	}

	@Override
	public long exception() {
		return this.exception / this.index;
	}

	public long getException() {
		return this.exception();
	}

	@Override
	public void clear() {
	}
}
