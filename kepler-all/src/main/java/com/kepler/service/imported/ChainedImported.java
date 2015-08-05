package com.kepler.service.imported;

import java.util.List;

import com.kepler.service.Imported;

/**
 * @author kim 2015年7月24日
 */
public class ChainedImported implements Imported {

	private final List<Imported> imported;

	public ChainedImported(List<Imported> imported) {
		super();
		this.imported = imported;
	}

	@Override
	public void subscribe(Class<?> service, String version) throws Exception {
		for (Imported each : this.imported) {
			each.subscribe(service, version);
		}
	}
}
