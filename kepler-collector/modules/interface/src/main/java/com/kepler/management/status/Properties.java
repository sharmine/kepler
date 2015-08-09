package com.kepler.management.status;

import java.util.Map;

import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年8月9日
 */
@Version("0.0.1")
public interface Properties {

	public Map<String, String> properties(String host);
}
