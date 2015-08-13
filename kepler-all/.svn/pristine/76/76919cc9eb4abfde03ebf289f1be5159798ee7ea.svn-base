package com.kepler.header.impl;

import com.kepler.header.Headers;
import com.kepler.header.HeadersHacker;
import com.kepler.host.Host;

/**
 * @author kim 2015年8月3日
 */
public class TagHacker implements HeadersHacker {

	@Override
	public Headers put(Headers headers) {
		headers.put(Host.TAG_KEY, Host.TAG.trim());
		return headers;
	}

	@Override
	public int sort() {
		// 最小优先级,最后执行
		return Integer.MIN_VALUE;
	}
}
