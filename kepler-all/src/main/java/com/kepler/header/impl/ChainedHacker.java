package com.kepler.header.impl;

import java.util.ArrayList;
import java.util.List;

import com.kepler.extension.Extension;
import com.kepler.header.Headers;
import com.kepler.header.HeadersHacker;

/**
 * @author kim 2015年7月14日
 */
public class ChainedHacker implements HeadersHacker, Extension {

	private final List<HeadersHacker> hackers = new ArrayList<HeadersHacker>();

	@Override
	// 将本次迭代返回Headers作为下次迭代参数Headers(便于包装Headers)
	public Headers put(Headers headers) {
		Headers each = headers;
		for (HeadersHacker hacker : this.hackers) {
			each = hacker.put(each);
		}
		return each;
	}

	@Override
	public ChainedHacker install(Object instance) {
		this.hackers.add(HeadersHacker.class.cast(instance));
		return this;
	}

	@Override
	public Class<?> interested() {
		return HeadersHacker.class;
	}
}
