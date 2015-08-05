package com.kepler.zookeeper;

import java.io.Serializable;

import com.kepler.host.Host;

/**
 * @author kim 2015年7月20日
 */
public class ZkSerial implements Serializable {

	private final static long serialVersionUID = 1L;

	private final Host host;

	private final String version;

	private final String service;

	public ZkSerial(Host host, String version, Class<?> service) {
		super();
		this.host = host;
		this.version = version;
		this.service = service.getName();
	}

	public Host host() {
		return this.host;
	}

	public String version() {
		return this.version;
	}

	public boolean version(String version) {
		return this.version.equals(version);
	}

	public Class<?> service() throws Exception {
		return Class.forName(this.service);
	}
}