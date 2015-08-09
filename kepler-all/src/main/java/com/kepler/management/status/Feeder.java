package com.kepler.management.status;

import java.util.Map;

import com.kepler.host.Host;
import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年8月8日
 */
@Version("0.0.1")
public interface Feeder {

	public void feed(Host local, Map<String, Object> status);
}
