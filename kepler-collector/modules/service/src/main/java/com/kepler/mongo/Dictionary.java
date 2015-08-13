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

	public final static String FIELD_HOSTS = "hosts";

	public final static String FIELD_TOTAL = "total";

	public final static String FIELD_STATUS = "status";

	public final static String FIELD_MINUTE = "minute";

	public final static String FIELD_METHOD = "method";

	public final static String FIELD_SERVICE = "service";

	public final static String FIELD_VERSION = "version";

	public final static String FIELD_TIMEOUT = "timeout";

	public final static String FIELD_EXCEPTION = "exception";

	public final static String FIELD_HOST_LOCAL = "host_local";

	public final static String FIELD_HOST_LOCAL_PID = "host_local_pid";

	public final static String FIELD_HOST_LOCAL_TAG = "host_local_tag";

	public final static String FIELD_HOST_LOCAL_GROUP = "host_local_group";

	public final static String FIELD_HOST_TARGET = "host_target";

	public final static String FIELD_HOST_TARGET_PID = "host_source_pid";

	public final static String FIELD_HOST_TARGET_TAG = "host_source_tag";

	public final static String FIELD_HOST_TARGET_GROUP = "host_target_group";

	public Object get(String key);
}
