package com.kepler.header;

import java.io.Serializable;

/**
 * @author kim 2015年7月14日
 */
public interface Headers extends Serializable {

	public String get(String key);

	public String get(String key, String def);

	/**
	 * @param key
	 * @param value
	 * @return value
	 */
	public String put(String key, String value);
}
