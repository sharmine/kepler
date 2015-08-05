package com.kepler.header.impl;

import com.kepler.header.Headers;
import com.kepler.header.HeadersHacker;
import com.kepler.host.Host;

/**
 * @author kim 2015年8月3日
 */
public class HostTagHacker implements HeadersHacker {

	@Override
	public Headers put(Headers headers) {
		headers.put(Host.TAG_KEY, Host.TAG.trim());
		return headers;
	}
}
