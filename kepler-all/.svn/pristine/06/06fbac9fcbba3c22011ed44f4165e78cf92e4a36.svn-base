package com.kepler.header.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.kepler.header.Headers;

/**
 * @author kim 2015年7月14日
 */
public class DefaultHeaders implements Headers {

	private final static long serialVersionUID = 1L;

	private final ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<String, String>();

	@Override
	public String get(String key) {
		return this.headers.get(key);
	}

	public String get(String key, String def) {
		String value = this.get(key);
		return value != null ? value : def;
	}

	@Override
	public String put(String key, String value) {
		return this.headers.putIfAbsent(key, value);
	}
}
