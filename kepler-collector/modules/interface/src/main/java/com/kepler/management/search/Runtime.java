package com.kepler.management.search;

import java.util.Map;

import com.kepler.annotation.Version;

/**
 * @author kim 2015年8月9日
 */
@Version("0.0.1")
public interface Runtime {

	public Map<String, String> runtime(String host);
}
