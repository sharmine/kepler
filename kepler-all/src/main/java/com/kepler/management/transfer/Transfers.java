package com.kepler.management.transfer;

import java.io.Serializable;
import java.util.Collection;

/**
 * Service, Version, Method维度
 * 
 * @author kim 2015年7月24日
 */
public interface Transfers extends Serializable {

	public String service();

	public String version();

	public String method();

	public Collection<Transfer> transfer();
}
