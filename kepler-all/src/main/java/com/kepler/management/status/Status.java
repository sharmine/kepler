package com.kepler.management.status;

import java.util.Map;

import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年8月8日
 */
@Version("0.0.1")
public interface Status {

	public Map<String, Object> get();
}
