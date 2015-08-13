package com.kepler.service.exported;

import com.kepler.invoker.Invoker;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;
import com.kepler.org.apache.commons.lang.reflect.MethodUtils;
import com.kepler.protocol.Request;
import com.kepler.service.Exported;
import com.kepler.service.ExportedContext;

/**
 * @author kim 2015年7月8日
 */
public class DefaultContext implements ExportedContext, Exported {

	private final MultiKeyMap services = new MultiKeyMap();

	public Invoker get(Class<?> clazz, String version) {
		return Invoker.class.cast(this.services.get(clazz, version));
	}

	@Override
	public void exported(Class<?> service, String version, Object instance) {
		this.services.put(service, version, new ProxyInvoker(instance));
	}

	private class ProxyInvoker implements Invoker {

		private final Object service;

		public ProxyInvoker(Object service) {
			super();
			this.service = service;
		}

		@Override
		public Object invoke(Request request) throws Exception {
			return MethodUtils.invokeMethod(this.service, request.method(), request.args());
		}
	}
}
