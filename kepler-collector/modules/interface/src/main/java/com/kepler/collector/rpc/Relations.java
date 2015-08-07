package com.kepler.collector.rpc;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author kim 2015年8月6日
 */
public interface Relations extends Serializable{

	public Collection<String> getImported();
	
	public Collection<String> getExported();
}
