package com.kepler.host;

import java.util.Collection;

/**
 * @author kim 2015年7月9日
 */
public interface HostsContext {

	/**
	 * 对于所有Hosts进行Ban操作(指定Host所有服务均离线)
	 * 
	 * @param host
	 */
	public void ban(Host host);

	/**
	 * 仅移除路由, 并不中断连接
	 * 
	 * @param host
	 */
	public void del(Host host);

	public void unban(Host host);

	public Hosts get(Class<?> service, String version);

	/**
	 * Hosts 4 all service
	 * 
	 * @return
	 */
	public Collection<Hosts> hosts();
}
