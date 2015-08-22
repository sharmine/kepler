package com.kepler.thread;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月16日
 */
public class ThreadFactory implements FactoryBean<ThreadPoolExecutor> {

	private final static Log LOGGER = LogFactory.getLog(ThreadFactory.class);

	private final static int MAX = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".max", "100"));

	private final static int CORE = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".core", "25"));

	private final static int QUEUE = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".queue", "50"));

	private final static int INTERVAL = Integer.valueOf(PropertiesUtils.get(ThreadFactory.class.getName().toLowerCase() + ".interval", "10"));

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

	public void destroy() throws Exception {
		this.destroy(this.threads.shutdownNow());
		while (!this.threads.awaitTermination(ThreadFactory.INTERVAL, TimeUnit.SECONDS)) {
			ThreadFactory.LOGGER.warn("Threads closing ... (" + new Date() + " )");
		}
	}

	private void destroy(List<Runnable> runnables) {
		for (Runnable each : runnables) {
			ThreadFactory.LOGGER.warn("Threads shutdown, lossing " + each.getClass() + " ... ");
		}
	}
}
