package com.kepler.protocol.impl;

import java.lang.reflect.Method;

import com.kepler.header.HeadersContext;
import com.kepler.header.HeadersHacker;
import com.kepler.protocol.Request;
import com.kepler.protocol.RequestFactory;

/**
 * @author kim 2015年7月8日
 */
public class DefaultRequestFactory implements RequestFactory {

	private final HeadersContext context;

	private final HeadersHacker hacker;

	public DefaultRequestFactory(HeadersContext context, HeadersHacker hacker) {
		super();
		this.context = context;
		this.hacker = hacker;
	}

	public Request request(Class<?> service, String version, Method method, Object[] args) {
		return new DefaultRequest(this.hacker.put(this.context.get()), service, version, method.getName(), args);
	}
}
