package com.kepler.collector.rpc.impl;

import java.util.Collection;

import com.kepler.collector.rpc.Relations;

/**
 * @author kim 2015年8月6日
 */
public class DefaultRelations implements Relations {

	private static final long serialVersionUID = 1L;

	private final Collection<String> importeds;

	private final Collection<String> exporteds;

	public DefaultRelations(Collection<String> importeds, Collection<String> exporteds) {
		super();
		this.importeds = importeds;
		this.exporteds = exporteds;
	}

	@Override
	public Collection<String> getImported() {
		return this.importeds;
	}

	@Override
	public Collection<String> getExported() {
		return this.exporteds;
	}
}