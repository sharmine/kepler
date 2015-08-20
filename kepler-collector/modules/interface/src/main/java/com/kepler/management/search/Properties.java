package com.kepler.management.search;

import java.util.Map;

import com.kepler.annotation.Service;

/**
 * @author kim 2015年8月9日
 */
@Service(version = "0.0.1")
public interface Properties {

	public Map<String, String> properties(String host);
}
