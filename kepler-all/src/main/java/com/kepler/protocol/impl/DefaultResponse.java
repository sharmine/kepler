package com.kepler.protocol.impl;

import com.kepler.config.PropertiesUtils;
import com.kepler.protocol.Response;

/**
 * @author kim 2015年7月8日
 */
public class DefaultResponse implements Response {

	private final static boolean TRACE = Boolean.valueOf(PropertiesUtils.get(DefaultResponse.class.getName().toLowerCase() + ".trace", "false"));

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
		this.throwable = throwable != null ? new StringBuffer().append(throwable.getClass()).append(": ").append(throwable.getMessage()).append(DefaultResponse.TRACE ? this.trace(throwable) : "").toString() : null;
	}

	private String trace(Throwable throwable) {
		StackTraceElement[] element = throwable.getStackTrace();
		return element != null && element.length > 0 ? new StringBuffer().append(" (Class: ").append(element[0].getClassName()).append(" ,Method: ").append(element[0].getMethodName()).append(" ,Line: ").append(element[0].getLineNumber()).append(") ").toString() : "";
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
