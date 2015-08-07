package com.kepler.collector.rpc;

import java.io.Serializable;

import com.kepler.host.Host;

/**
 * Host/Source, Host/Target维度
 * 
 * @author kim 2015年7月24日
 */
public interface Condition extends Serializable {

	public Host host();

	public Host local();

	/**
	 * Round trip time
	 * 
	 * @return
	 */
	public long rtt();

	/**
	 * 累计数量
	 * 
	 * @return
	 */
	public long total();

	/**
	 * 超时数量
	 * 
	 * @return
	 */
	public long timeout();

	/**
	 * 异常数量
	 * 
	 * @return
	 */
	public long exception();

	public void clear();
}
