package com.kepler.zookeeper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.FactoryBean;

import com.kepler.config.PropertiesUtils;
import com.kepler.zookeeper.processor.Processor;
import com.kepler.zookeeper.processor.Processor.StateMatch;

/**
 * @author kim 2015年7月8日
 */
public class ZkFactory implements FactoryBean<ZooKeeper> {

	private final static int TIMEOUT = Integer.valueOf(PropertiesUtils.get(ZkFactory.class.getName().toLowerCase() + ".timeout", "120000"));

	private final static String HOST = PropertiesUtils.get(ZkFactory.class.getName().toLowerCase() + ".host");

	private final static Log LOGGER = LogFactory.getLog(ZkFactory.class);

	private final Map<StateMatch, Processor> processor = new ConcurrentHashMap<StateMatch, Processor>();

	private final ZkConnection connection;

	private final String address;

	private ZooKeeper zoo;

	/**
	 * @param address ZK地址, 为Imported体系外服务预留
	 * @param connection
	 * @param processor
	 */
	public ZkFactory(String address, ZkConnection connection, Set<Processor> processor) {
		super();
		this.address = address;
		this.connection = connection;
		for (Processor each : processor) {
			this.processor.put(each.match(), each);
		}
	}

	public ZkFactory(ZkConnection connection, Set<Processor> processor) {
		this(ZkFactory.HOST, connection, processor);
	}

	public void init() throws Exception {
		this.zoo = new ZooKeeper(this.address, ZkFactory.TIMEOUT, new ZkWatcher());
		this.connection.await();
	}

	public void destory() throws Exception {
		this.zoo.close();
	}

	@Override
	public ZooKeeper getObject() throws Exception {
		return this.zoo;
	}

	@Override
	public Class<?> getObjectType() {
		return ZooKeeper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private class ZkWatcher implements Watcher {

		@Override
		public void process(WatchedEvent event) {
			Processor processor = ZkFactory.this.processor.get(new StateMatch(event.getState(), event.getType()));
			if (processor != null) {
				processor.process(event.getPath());
			} else {
				ZkFactory.LOGGER.warn("None processor for event " + event);
			}
		}
	}
}
