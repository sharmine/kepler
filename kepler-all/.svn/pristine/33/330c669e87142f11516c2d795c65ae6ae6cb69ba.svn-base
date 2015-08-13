package com.kepler.header.impl;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.kepler.extension.Extension;
import com.kepler.header.Headers;
import com.kepler.header.HeadersHacker;

/**
 * @author kim 2015年7月14日
 */
public class ChainedHacker implements HeadersHacker, Extension {

	private final Set<HeadersHacker> hackers = new TreeSet<HeadersHacker>(new Comparator<HeadersHacker>() {
		public int compare(HeadersHacker o1, HeadersHacker o2) {
			return o1.sort() - o2.sort();
		}
	});

	@Override
	// 将本次迭代返回Headers作为下次迭代参数Headers(便于包装Headers)
	public Headers put(Headers headers) {
		Headers each = headers;
		for (HeadersHacker hacker : this.hackers) {
			each = hacker.put(each);
		}
		return each;
	}

	public int sort() {
		return Integer.MIN_VALUE;
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
