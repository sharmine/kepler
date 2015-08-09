package com.kepler.management.transfer;

import java.util.Collection;

import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年7月22日
 */
@Version("0.0.1")
public interface Feeder {
	
	public void feed(Collection<Transfers> transfer);
}
