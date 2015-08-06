package com.kepler.mongo;

/**
 * 
 * @author kim 2013-11-15
 */
public interface Dictionary {
	/**
	 * 数据库名称
	 */
	public final static String D_NAME = "D_NAME";
	/**
	 * 数据集合名称
	 */
	public final static String C_NAME = "C_NAME";

	public final static String FIELD_ID = "_id";

	public final static String FIELD_RTT = "rtt";
	
	public final static String FIELD_TOTAL = "total";
	
	public final static String FIELD_MINUTE = "minute";

	public final static String FIELD_METHOD = "method";
	
	public final static String FIELD_SERVICE = "service";

	public final static String FIELD_VERSION = "version";

	public final static String FIELD_TIMEOUT = "timeout";

	public final static String FIELD_EXCEPTION = "exception";

	public final static String FIELD_HOST_SOURCE = "host_source";

	public final static String FIELD_HOST_TARGET = "host_target";
	
	public Object get(String key);
}
