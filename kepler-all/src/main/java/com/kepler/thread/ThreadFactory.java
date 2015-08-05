package com.kepler.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.FactoryBean;

import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月16日
 */
public class ThreadFactory implements FactoryBean<ThreadPoolExecutor> {

	private final static int MAX = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".max", "50"));

	private final static int CORE = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".core", "25"));

	private final static int QUEUE = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".queue", "20"));

	private final static int KEEPALIVE = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".keepalive", "60000"));

	private ThreadPoolExecutor threads;

	@Override
	public ThreadPoolExecutor getObject() throws Exception {
		return this.threads;
	}

	@Override
	public Class<ThreadPoolExecutor> getObjectType() {
		return ThreadPoolExecutor.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void init() {
		this.threads = new ThreadPoolExecutor(ThreadFactory.CORE, ThreadFactory.MAX, ThreadFactory.KEEPALIVE, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(ThreadFactory.QUEUE), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void destory() {
		this.threads.shutdown();
	}
}
