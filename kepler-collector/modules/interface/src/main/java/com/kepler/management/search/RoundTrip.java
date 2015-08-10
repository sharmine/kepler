package com.kepler.management.search;

import java.util.Collection;

import com.kepler.annotation.Version;
import com.kepler.management.transfer.Transfers;

/**
 * @author kim 2015年7月25日
 */
@Version("0.0.1")
public interface RoundTrip {

	/**
	 * @param service
	 * @param version
	 * @param host 服务所属主机
	 * @return
	 */
	public Collection<Transfers> roundtrip(String service, String version, String host);
}
