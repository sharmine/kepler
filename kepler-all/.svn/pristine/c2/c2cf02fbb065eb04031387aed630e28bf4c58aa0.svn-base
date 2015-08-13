package com.kepler.invoker.impl;

import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.config.PropertiesUtils;
import com.kepler.invoker.Invoker;
import com.kepler.protocol.Request;

/**
 * 限流
 * 
 * @author kim 2015年7月8日
 */
public class ThresholdInvoker implements Invoker {

	private final static int THRESHOLD = Integer.valueOf(PropertiesUtils.get(ThresholdInvoker.class.getName().toLowerCase() + ".threshold", "1000"));

	private final static Log LOGGER = LogFactory.getLog(ThresholdInvoker.class);

	private final Semaphore semaphore = new Semaphore(ThresholdInvoker.THRESHOLD);

	private final Invoker delegate;

	public ThresholdInvoker(Invoker delegate) {
		super();
		this.delegate = delegate;
	}

	public Object invoke(Request request) throws Exception {
		try {
			this.semaphore.acquire();
			return this.delegate.invoke(request);
		} finally {
			this.semaphore.release();
			ThresholdInvoker.LOGGER.debug("Release semaphore " + this.semaphore.availablePermits());
		}
	}
}
