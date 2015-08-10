package com.kepler.service.exported;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationUtils;

import com.kepler.annotation.Version;
import com.kepler.service.Exported;

/**
 * @author kim 2015年7月8日
 */
public class ExportedServiceFactory<T> implements FactoryBean<T> {

	private final Class<T> service;

	private final T instance;

	public ExportedServiceFactory(Exported exported, Class<T> service, T instance) throws Exception {
		this(exported, service, AnnotationUtils.findAnnotation(instance.getClass(), Version.class).value(), instance);
	}

	public ExportedServiceFactory(Exported exported, Class<T> service, String version, T instance) throws Exception {
		super();
		this.service = service;
		this.instance = instance;
		exported.exported(service, version, instance);
	}

	@Override
	public T getObject() throws Exception {
		return this.instance;
	}

	@Override
	public Class<T> getObjectType() {
		return this.service;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
