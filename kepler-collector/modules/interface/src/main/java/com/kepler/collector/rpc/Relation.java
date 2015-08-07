package com.kepler.collector.rpc;

import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年8月6日
 */
@Version("0.0.1")
public interface Relation {

	public Relations relation(String host);
}
