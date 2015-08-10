package com.kepler.service.imported;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.annotation.AnnotationUtils;

import com.kepler.annotation.Version;
import com.kepler.invoker.Invoker;
import com.kepler.protocol.RequestFactory;
import com.kepler.service.Imported;

/**
 * @author kim 2015年7月8日
 */
public class ImportedServiceFactory<T> implements FactoryBean<T> {

	private final CglibProxy proxy = new CglibProxy();

	private final RequestFactory factory;

	private final List<Method> methods;

	private final Imported imported;

	private final Class<T> service;

	private final Invoker invoker;

	private final String version;

	public ImportedServiceFactory(Class<T> service, Invoker invoker, RequestFactory factory, Imported imported) {
		this(service, AnnotationUtils.findAnnotation(service, Version.class).value(), invoker, factory, imported);
	}

	public ImportedServiceFactory(Class<T> service, String version, Invoker invoker, RequestFactory factory, Imported imported) {
		super();
		this.service = service;
		this.version = version;
		this.invoker = invoker;
		this.factory = factory;
		this.imported = imported;
		this.methods = Arrays.asList(this.service.getMethods());
	}

	public T getObject() throws Exception {
		this.imported.subscribe(this.service, this.version);
		return this.proxy.getProxy(this.service);
	}

	public Class<?> getObjectType() {
		return this.service;
	}

	public boolean isSingleton() {
		return true;
	}

	private class CglibProxy implements MethodInterceptor {

		private Enhancer enhancer = new Enhancer();

		@SuppressWarnings("unchecked")
		public T getProxy(Class<T> clazz) {
			this.enhancer.setCallback(this);
			this.enhancer.setSuperclass(clazz);
			return (T) this.enhancer.create();
		}

		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			return ImportedServiceFactory.this.methods.contains(method) ? this.invoke(method, args) : proxy.invokeSuper(obj, args);
		}

		private Object invoke(Method method, Object[] args) throws Exception {
			return ImportedServiceFactory.this.invoker.invoke(ImportedServiceFactory.this.factory.request(ImportedServiceFactory.this.service, ImportedServiceFactory.this.version, method, args));
		}
	}
}
