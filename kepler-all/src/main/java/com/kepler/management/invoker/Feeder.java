package com.kepler.management.invoker;

import java.util.Collection;

import com.kepler.annotation.Service;
import com.kepler.host.Host;

/**
 * @author kim 2015年8月8日
 */
@Service(version = "0.0.1")
public interface Feeder {

	public void feed(Host local, Collection<Host> invokers);
}
