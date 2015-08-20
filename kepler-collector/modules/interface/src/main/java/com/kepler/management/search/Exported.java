package com.kepler.management.search;

import java.util.Collection;

import com.kepler.annotation.Service;
import com.kepler.host.Host;

/**
 * @author kim 2015年8月8日
 */
@Service(version = "0.0.1")
public interface Exported {

	public Collection<Host> exported(String host);
}
