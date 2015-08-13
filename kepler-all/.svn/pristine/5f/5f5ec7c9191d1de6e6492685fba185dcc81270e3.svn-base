package com.kepler.protocol;

import java.io.Serializable;

import com.kepler.header.Headers;

/**
 * @author kim 2015年7月8日
 */
public interface Request extends Serializable {

	public Class<?> service();

	public String version();

	public String method();

	public Object[] args();

	public String ack();

	public Headers headers();
}
