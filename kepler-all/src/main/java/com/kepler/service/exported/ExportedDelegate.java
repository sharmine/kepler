package com.kepler.service.exported;

import org.springframework.core.annotation.AnnotationUtils;

import com.kepler.annotation.Service;
import com.kepler.service.Exported;

/**
 * @author kim 2015年7月8日
 */
public class ExportedDelegate {

	private final Exported exported;

	private final Class<?> service;

	private final Object instance;

	private final String version;

	public ExportedDelegate(Exported exported, Class<?> service, Object instance) throws Exception {
		this(exported, service, AnnotationUtils.findAnnotation(instance.getClass(), Service.class).version(), instance);
	}

	public ExportedDelegate(Exported exported, Class<?> service, String version, Object instance) throws Exception {
		super();
		this.service = service;
		this.version = version;
		this.exported = exported;
		this.instance = instance;
	}

	public void init() throws Exception {
		this.exported.exported(this.service, this.version, this.instance);
	}
}
