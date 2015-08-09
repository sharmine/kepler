package com.kepler.management.transfer;

import java.util.Collection;

import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年8月8日
 */
@Version("0.0.1")
public interface Relations {

	public Collection<String> relations(String service, String version, String target);
}
