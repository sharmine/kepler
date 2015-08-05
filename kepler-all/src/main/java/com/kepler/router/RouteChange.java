package com.kepler.router;

/**
 * @author kim 2015年7月12日
 */
public interface RouteChange {

	public enum Action {

		PUSH, DEL;
	}

	public String path();

	public Action action();
}
