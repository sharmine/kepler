package com.kepler.service;

/**
 * @author kim 2015年7月3日
 */
public interface Exported {

	public void exported(Class<?> service, String version, Object instance) throws Exception;
}
