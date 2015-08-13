package com.kepler.protocol.impl;

import com.kepler.protocol.Response;

/**
 * @author kim 2015年7月8日
 */
public class DefaultResponse implements Response {

	private final static long serialVersionUID = 1L;

	private final String ack;

	private final Object response;

	private final String throwable;

	public DefaultResponse(String ack, Object response) {
		this(ack, response, null);
	}

	public DefaultResponse(String ack, Throwable throwable) {
		this(ack, null, throwable);
	}

	private DefaultResponse(String ack, Object response, Throwable throwable) {
		super();
		this.ack = ack;
		this.response = response;
		this.throwable = throwable != null ? (throwable.getClass() + ": " + throwable.getMessage()) : null;
	}

	@Override
	public String ack() {
		return this.ack;
	}

	@Override
	public Object response() {
		return this.response;
	}

	public String throwable() {
		return this.throwable;
	}

	public boolean valid() {
		return this.throwable() == null;
	}
}
