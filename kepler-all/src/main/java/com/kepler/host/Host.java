package com.kepler.host;

import java.io.Serializable;

import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月8日
 */
public interface Host extends Serializable {

	public final static String LOOP = "localhost";

	/**
	 * 主机默认Tag值
	 */
	public final static String TAG_DEFAULT = "";

	public final static String TAG_KEY = Host.class.getName().toLowerCase() + ".tag";

	/**
	 * 主机当前Tag值(JVM启动时初始, 服务生命周期内部不可变)
	 */
	public final static String TAG = PropertiesUtils.get(Host.TAG_KEY, Host.TAG_DEFAULT);

	public final static String GROUP_DEFAULT = "unknow";

	public final static String GROUP = PropertiesUtils.get(Host.class.getName().toLowerCase() + ".group", System.getenv("USER") != null ? System.getenv("USER") : Host.GROUP_DEFAULT);

	public int port();
	
	public String pid();

	public String tag();

	public String host();

	public String group();
	
	public String getAsString();

	public boolean loop(Host host);

	public boolean loop(String host);
}
