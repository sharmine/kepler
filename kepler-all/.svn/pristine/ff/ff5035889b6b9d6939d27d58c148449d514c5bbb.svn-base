package com.kepler.protocol.impl;

import java.util.UUID;

import com.kepler.header.Headers;
import com.kepler.protocol.Request;

/**
 * @author kim 2015年7月8日
 */
public class DefaultRequest implements Request {

	private final static long serialVersionUID = 1L;

	private final String ack = UUID.randomUUID().toString();

	private final Headers headers;

	private final Class<?> service;

	private final String version;

	private final String method;

	private final Object[] args;

	public DefaultRequest(Headers headers, Class<?> service, String version, String method, Object[] args) {
		super();
		this.args = args;
		this.method = method;
		this.headers = headers;
		this.version = version;
		this.service = service;
	}

	public Class<?> service() {
		return this.service;
	}

	public String version() {
		return this.version;
	}

	public String method() {
		return this.method;
	}

	public Object[] args() {
		return this.args;
	}

	public String ack() {
		return this.ack;
	}

	@Override
	public Headers headers() {
		return this.headers;
	}
}