package com.kepler.protocol;

import java.lang.reflect.Method;

/**
 * @author kim 2015年7月8日
 */
public interface RequestFactory {

	public Request request(Class<?> service, String version, Method method, Object[] args);
}
