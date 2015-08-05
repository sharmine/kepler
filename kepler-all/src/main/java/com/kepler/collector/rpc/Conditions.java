package com.kepler.collector.rpc;

import java.io.Serializable;
import java.util.Collection;

import com.kepler.ack.Status;
import com.kepler.host.Host;

/**
 * @author kim 2015年7月24日
 */
public interface Conditions extends Serializable {

	public String service();

	public String version();
	
	public String method();

	public Collection<Condition> conditions();
	
	public void clear();

	public void put(Host source, Host target, Status status, long rtt);
}
