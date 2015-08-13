package com.kepler.management.status.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.kepler.management.status.Status;

/**
 * @author kim 2015年8月10日
 */
public class ChainedStatus implements Status {

	private final Collection<Status> status;

	public ChainedStatus(Collection<Status> status) {
		super();
		this.status = status;
	}

	@Override
	public Map<String, Object> get() {
		Map<String, Object> status = new HashMap<String, Object>();
		for (Status each : this.status) {
			status.putAll(each.get());
		}
		return status;
	}
}
