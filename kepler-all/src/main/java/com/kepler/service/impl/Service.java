package com.kepler.service.impl;

/**
 * @author kim 2015年7月24日
 */
public class Service {

	private final Class<?> service;

	private final String version;

	public Service(Class<?> service, String version) {
		super();
		this.service = service;
		this.version = version;
	}

	public Class<?> service() {
		return this.service;
	}

	public String version() {
		return this.version;
	}

	public int hashCode() {
		return this.service.hashCode() ^ this.version.hashCode();
	}

	public boolean equals(Object ob) {
		Service service = Service.class.cast(ob);
		return this.service.equals(service.service) && this.version.equals(service.version);
	}
}