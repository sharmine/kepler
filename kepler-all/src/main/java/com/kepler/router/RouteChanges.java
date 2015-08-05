package com.kepler.router;

/**
 * @author kim 2015年7月10日
 */
public interface RouteChanges {

	public void put(RouteChange change);

	public RouteChange get() throws Exception;
}
