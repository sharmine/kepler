package com.kepler.collector;

import java.util.Collection;

import com.kepler.collector.rpc.Condition;
import com.kepler.service.annotation.Version;

/**
 * @author kim 2015年7月25日
 */
@Version("0.0.1")
public interface History {

	/**
	 * @param service
	 * @param version
	 * @param host 服务所属主机
	 * @return
	 */
	public Collection<Condition> last(String service, String version, String host);
}
