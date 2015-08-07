package com.kepler.collector.rpc;

import java.io.Serializable;
import java.util.Collection;

/**
 * Service, Version, Method维度
 * 
 * @author kim 2015年7月24日
 */
public interface Conditions extends Serializable {

	public String service();

	public String version();

	public String method();

	public Collection<Condition> conditions();
}
