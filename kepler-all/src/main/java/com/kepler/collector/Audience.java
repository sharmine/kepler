package com.kepler.collector;

import java.util.Collection;

import com.kepler.collector.rpc.Conditions;
import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年7月22日
 */
@Version("0.0.1")
public interface Audience {

	public void put(Collection<Conditions> conditions);
}
