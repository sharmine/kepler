package com.kepler.management.search;

import java.util.Map;

import com.kepler.annotation.Version;

/**
 * @author kim 2015年8月9日
 */
@Version("0.0.1")
public interface Properties {

	public Map<String, String> properties(String host);
}
