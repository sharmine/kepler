package com.kepler.protocol.impl;

import com.kepler.protocol.Response;
import com.kepler.protocol.ResponseFactory;

/**
 * @author kim 2015年7月8日
 */
public class DefaultResponseFactory implements ResponseFactory {

	@Override
	public Response response(String ack, Object response) {
		return new DefaultResponse(ack, response);
	}

	public Response throwable(String ack, Throwable throwable) {
		return new DefaultResponse(ack, this.cause(throwable));
	}

	private Throwable cause(Throwable throwable) {
		return throwable.getCause() == null ? throwable : this.cause(throwable.getCause());
	}
}
