package com.kepler.collector;

import java.util.Collection;

import com.kepler.collector.rpc.Conditions;
import com.kepler.collector.runtime.State;
import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年7月22日
 */
@Version("0.0.1")
public interface Note {

	public void state(Collection<State> states);

	public void condition(Collection<Conditions> conditions);
}
