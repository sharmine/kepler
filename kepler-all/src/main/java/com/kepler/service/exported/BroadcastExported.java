package com.kepler.service.exported;

import java.util.List;

import com.kepler.service.Exported;

/**
 * @author kim 2015年7月8日
 */
public class BroadcastExported implements Exported {

	private final List<Exported> exported;

	public BroadcastExported(List<Exported> exported) {
		super();
		this.exported = exported;
	}

	@Override
	public void exported(Class<?> service, String version, Object instance) throws Exception {
		for (Exported each : this.exported) {
			each.exported(service, version, instance);
		}
	}
}
