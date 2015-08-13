package com.kepler.management.status.impl;

import java.util.HashMap;
import java.util.Map;

import com.kepler.config.PropertiesUtils;
import com.kepler.management.status.Status;

/**
 * @author kim 2015年8月10日
 */
public class PropertiesStatus implements Status {

	private final Map<String, Object> param = new HashMap<String, Object>();

	public void init() {
		for (String key : PropertiesUtils.used().keySet()) {
			this.param.put("P_" + key.toUpperCase().replaceAll("\\.", "_"), PropertiesUtils.used().get(key));
		}
	}

	@Override
	public Map<String, Object> get() {
		return this.param;
	}

}
