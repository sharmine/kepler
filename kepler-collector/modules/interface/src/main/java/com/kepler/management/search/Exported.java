package com.kepler.management.search;

import java.util.Collection;

import com.kepler.annotation.Version;

/**
 * @author kim 2015年8月8日
 */
@Version("0.0.1")
public interface Exported {

	public Collection<String> exported(String service, String version, String host);
}
