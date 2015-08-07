package com.kepler.collector.rpc;

import java.util.Collection;

import com.kepler.ack.Ack;

/**
 * @author kim 2015年7月21日
 */
public interface Collector {

	public void collect(Ack ack);

	public Collection<Notes> conditions();
}
