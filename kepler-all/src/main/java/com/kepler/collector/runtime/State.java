package com.kepler.collector.runtime;

import java.util.Set;

/**
 * @author kim 2015年8月1日
 */
public interface State {

	public void put(String key, String value);

	public String get(String key);

	public Set<String> keys();
}
