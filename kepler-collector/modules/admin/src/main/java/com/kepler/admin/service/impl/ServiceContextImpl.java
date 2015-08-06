package com.kepler.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.StringUtils;

import com.kepler.admin.service.Node;
import com.kepler.admin.service.Path;
import com.kepler.admin.service.Paths;
import com.kepler.admin.service.ServiceContext;
import com.kepler.host.Host;
import com.kepler.serial.SerialFactory;
import com.kepler.zookeeper.ZkContext;
import com.kepler.zookeeper.ZkSerial;

/**
 * @author kim 2015年7月20日
 */
public class ServiceContextImpl implements ServiceContext {

	private final static Log LOGGER = LogFactory.getLog(ServiceContextImpl.class);

	private final ZooKeeper zooKeeper;

	private final SerialFactory serial;

	public ServiceContextImpl(ZooKeeper zooKeeper, SerialFactory serial) {
		super();
		this.zooKeeper = zooKeeper;
		this.serial = serial;
	}

	@Override
	public Paths path(String path) {
		try {
			return new ZkPaths(path, this.zooKeeper.getChildren(ZkContext.ROOT + (StringUtils.isEmpty(path) ? "" : ("/" + path)), true));
		} catch (Exception e) {
			ServiceContextImpl.LOGGER.warn(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Node get(String path) {
		try {
			return new ZkNode(path, this.serial.serial(this.zooKeeper.getData(ZkContext.ROOT + "/" + path, false, null), ZkSerial.class).host());
		} catch (Exception e) {
			ServiceContextImpl.LOGGER.info(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public Node del(String path) throws Exception {
		Node node = this.get(path);
		this.zooKeeper.delete(ZkContext.ROOT + "/" + path, 0);
		return node;
	}

	private class ZkPaths extends ArrayList<Path> implements Paths {

		private final static long serialVersionUID = 1L;

		public ZkPaths(String parent, List<String> paths) {
			for (String each : paths) {
				super.add(new ZkPath(each, StringUtils.isEmpty(parent) ? each : (parent + "/" + each)));
			}
		}

	}

	private class ZkPath implements Path {

		private final String service;

		private final String path;

		private final Node node;

		public ZkPath(String service, String path) {
			super();
			this.service = service;
			this.path = path;
			this.node = ServiceContextImpl.this.get(this.getPath());
		}

		@Override
		public String getService() {
			return this.service;
		}

		@Override
		public String getPath() {
			return this.path;
		}

		public Node getNode() {
			return this.node;
		}

		@Override
		public boolean isDetail() {
			return this.getNode() != null;
		}
	}

	private class ZkNode implements Node {

		private final Host host;

		private final String path;

		public ZkNode(String path, Host host) {
			super();
			this.host = host;
			this.path = path;
		}

		public String getTag() {
			return this.host.tag();
		}

		public String getPath() {
			return this.path;
		}

		@Override
		public String getHost() {
			return this.host.host() + ":" + this.host.port();
		}

		@Override
		public String getHostAsString() {
			return this.getHost() + " (" + this.host.group() + ")";
		}
	}
}
