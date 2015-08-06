package com.kepler.admin.service;



/**
 * @author kim 2015年7月20日
 */
public interface ServiceContext {
	
	public Node get(String path) throws Exception;
	
	public Node del(String path) throws Exception;
	
	public Paths path(String path) throws Exception;
}
