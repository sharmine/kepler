package com.kepler.admin.service;


/**
 * @author kim 2015年7月20日
 */
public interface Path {

	public String getService();

	public String getPath();
	
	public Node getNode();
	
	public boolean isDetail();
}
