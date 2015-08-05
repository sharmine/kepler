package com.kepler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;

/**
 * @author kim 2015年7月13日
 */
public class Start {

	private final static Log LOGGER = LogFactory.getLog(Start.class);

	public static void main(String[] args) throws Exception {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:" + PropertiesUtils.get(Start.class.getName().toLowerCase() + ".xml"))) {
			Runtime.getRuntime().addShutdownHook(new Shutdown(context));
			Start.wait(context);
			Start.LOGGER.warn("Kepler + (" + context.getBean(Host.class).getAsString() + ") closed ...");
		} catch (Throwable e) {
			e.printStackTrace();
			Start.LOGGER.fatal(e.getMessage(), e);
			System.exit(1);
		}
	}

	private static void wait(ClassPathXmlApplicationContext context) throws InterruptedException {
		synchronized (context) {
			while (context.isActive()) {
				context.wait();
			}
		}
	}

	private static class Shutdown extends Thread {

		final ClassPathXmlApplicationContext context;

		public Shutdown(ClassPathXmlApplicationContext context) {
			super();
			this.context = context;
		}

		@Override
		public void run() {
			synchronized (this.context) {
				synchronized (context) {
					this.context.notifyAll();
				}
			}
		}
	}
}