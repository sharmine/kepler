package com.kepler.extension.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.kepler.extension.Extension;

/**
 * @author kim 2015年7月14日
 */
public class Extensions implements BeanPostProcessor {

	private final static Log LOGGER = LogFactory.getLog(Extensions.class);

	private final List<Extension> extensions;

	public Extensions(List<Extension> extensions) {
		super();
		this.extensions = extensions;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	// Warning: 对于FactryBean构造的Bean如果没有被引用将无法被PostProcessAfterInitialization触发
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		for (Extension each : this.extensions) {
			if (each.interested().isAssignableFrom(bean.getClass()) && !this.same(bean, each)) {
				each.install(bean);
				Extensions.LOGGER.info(each + " installed " + bean + " ... ");
			}
		}
		return bean;
	}

	/**
	 * 扩展点禁止安装自身
	 * 
	 * @param bean
	 * @param each
	 * @return
	 */
	private boolean same(Object bean, Extension each) {
		return each == bean;
	}
}
