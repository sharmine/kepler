package com.kepler.service.exported;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import com.kepler.KeplerException;
import com.kepler.annotation.Service;
import com.kepler.service.Exported;

/**
 * @author kim 2015年8月19日
 */
public class ExportedDiscovery implements BeanPostProcessor {

	private final Exported exported;

	public ExportedDiscovery(Exported exported) {
		super();
		this.exported = exported;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Service service = AnnotationUtils.findAnnotation(bean.getClass(), Service.class);
		if (service != null && service.autowired()) {
			this.exported(bean, service.version());
		}
		return bean;
	}

	private void exported(Object bean, String version) {
		for (Class<?> each : this.extract(new ArrayList<Class<?>>(), bean.getClass())) {
			try {
				this.exported.exported(each, version, bean);
			} catch (Exception e) {
				throw new KeplerException(e);
			}
		}
	}

	private Collection<Class<?>> extract(Collection<Class<?>> exported, Class<?> clazz) {
		this.recursive(exported, clazz);
		this.interfaces(exported, clazz);
		return exported;
	}

	private void recursive(Collection<Class<?>> exported, Class<?> clazz) {
		if (clazz.getSuperclass() != null) {
			this.extract(exported, clazz.getSuperclass());
		}
	}

	private void interfaces(Collection<Class<?>> exported, Class<?> clazz) {
		for (Class<?> each : clazz.getInterfaces()) {
			if (AnnotationUtils.findAnnotation(each, Service.class) != null) {
				exported.add(each);
			}
		}
	}
}
