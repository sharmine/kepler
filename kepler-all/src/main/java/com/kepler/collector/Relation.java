package com.kepler.collector;

import java.util.Collection;

import com.kepler.host.Host;

/**
 * @author kim 2015年8月5日
 */
public interface Relation {

	public Collection<Host> relation(String service, String version, String host);
}
