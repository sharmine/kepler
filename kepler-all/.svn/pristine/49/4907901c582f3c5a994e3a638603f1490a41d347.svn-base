package com.kepler.header.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.header.Headers;
import com.kepler.header.HeadersContext;

/**
 * 线程绑定Headers
 * 
 * @author kim 2015年7月14日
 */
public class ThreadContext implements HeadersContext {

	private final static Log LOGGER = LogFactory.getLog(ThreadContext.class);

	private final static ThreadLocal<Headers> HEADERS = new InheritableThreadLocal<Headers>() {
		// Callback when invoke GET
		protected Headers initialValue() {
			ThreadContext.LOGGER.debug("Create Headers ... ");
			return new DefaultHeaders();
		}
	};

	@Override
	public Headers get() {
		return HEADERS.get();
	}

	@Override
	public Headers put(Headers headers) {
		HEADERS.set(headers);
		return headers;
	}
}
