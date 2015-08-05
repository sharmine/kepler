package com.kepler.invoker;

import com.kepler.protocol.Request;

/**
 * @author kim 2015年7月8日
 */
public interface Invoker {

	public Object invoke(Request request) throws Exception;
}
