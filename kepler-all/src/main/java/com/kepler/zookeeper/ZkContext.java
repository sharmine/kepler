package com.kepler.zookeeper;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.StringUtils;

import com.kepler.config.PropertiesUtils;
import com.kepler.conncetion.Connect;
import com.kepler.host.Host;
import com.kepler.host.HostsContext;
import com.kepler.router.RouteChange;
import com.kepler.router.RouteChanges;
import com.kepler.serial.SerialFactory;
import com.kepler.service.Exported;
import com.kepler.service.Imported;

/**
 * @author kim 2015年7月9日
 */
public class ZkContext implements Imported, Exported {

	public final static String ROOT = PropertiesUtils.get(Roadmap.class.getName().toLowerCase() + ".root", "/kepler");

	public final static boolean WATCH = Boolean.valueOf(PropertiesUtils.get(Roadmap.class.getName().toLowerCase() + ".watch", "true"));

	/**
	 * 单线程防止线程冲突
	 */
	private final static ExecutorService SERVICE = Executors.newFixedThreadPool(1);

	private final static Log LOGGER = LogFactory.getLog(ZkContext.class);

	private final AtomicBoolean shutdown = new AtomicBoolean();

	private final Runnable changed = new ChangedRunnable();

	private final Exporteds exported = new Exporteds();

	private final Importeds imported = new Importeds();

	private final Roadmap road = new Roadmap();

	private final RouteChanges changes;

	private final HostsContext context;

	private final SerialFactory serial;

	private final Connect connect;

	private final ZooKeeper zoo;

	private final Host local;

	public ZkContext(HostsContext context, RouteChanges changes, SerialFactory serial, Connect connect, ZooKeeper zoo, Host local) {
		super();
		this.zoo = zoo;
		this.local = local;
		this.serial = serial;
		this.changes = changes;
		this.context = context;
		this.connect = connect;
	}

	public void init() {
		ZkContext.SERVICE.execute(this.changed);
	}

	public void destory() throws Exception {
		this.exported.destory();
		this.shutdown.set(true);
		ZkContext.SERVICE.shutdown();
	}

	private void put(ZkSerial node, String path) throws Exception {
		// 加锁Service all version (粗粒度)
		synchronized (node.service()) {
			// 同步GetOrCreate
			this.context.get(node.service(), node.version()).put(node.host());
		}
		this.imported.put(path, node.host());
		this.connect.connect(node.host());
	}

	private void importing(String services) throws Exception {
		for (String each : this.zoo.getChildren(services, ZkContext.WATCH)) {
			try {
				String path = services + "/" + each;
				// 监听指定版本, 无需node.version(version)
				this.put(this.serial.serial(this.zoo.getData(path, ZkContext.WATCH, null), ZkSerial.class), path);
			} catch (Exception e) {
				e.printStackTrace();
				ZkContext.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void subscribe(Class<?> service, String version) throws Exception {
		for (String each : this.zoo.getChildren(this.road.roadmap(this.road.path(service, version)), ZkContext.WATCH)) {
			try {
				String path = this.road.path(service, version, each);
				ZkSerial node = this.serial.serial(this.zoo.getData(path, ZkContext.WATCH, null), ZkSerial.class);
				if (node.version(version)) {
					this.put(node, path);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ZkContext.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void exported(Class<?> service, String version, Object instance) throws Exception {
		this.exported.put(this.zoo.create(this.road.roadmap(this.road.path(service, version)) + (ZkContext.ROOT + "_"), this.serial.serial(new ZkSerial(this.local, version, service)), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
	}

	private class Roadmap {

		public String path(Class<?> service, String... path) {
			StringBuffer buffer = new StringBuffer(ZkContext.ROOT).append("/").append(service.getName().replaceAll("\\.", "/")).append("/");
			for (String each : path) {
				buffer.append(each).append("/");
			}
			return buffer.substring(0, buffer.length() - 1);
		}

		public String roadmap(String path) throws Exception {
			StringBuffer buffer = new StringBuffer();
			for (String each : path.split("/")) {
				if (StringUtils.hasText(each)) {
					String road = buffer.append("/").append(each).toString();
					if (ZkContext.this.zoo.exists(road, ZkContext.WATCH) == null) {
						ZkContext.this.zoo.create(road, new byte[] {}, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					}
				}
			}
			return path;
		}
	}

	private class Exporteds {

		private final Set<String> exported = new CopyOnWriteArraySet<String>();

		public void put(String path) {
			this.exported.add(path);
		}

		public void destory() {
			for (String exported : this.exported) {
				try {
					ZkContext.this.zoo.delete(exported, 0);
				} catch (Exception e) {
					e.printStackTrace();
					ZkContext.LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	private class Importeds {

		private final Map<String, Host> imported = new ConcurrentHashMap<String, Host>();

		public void put(String path, Host host) {
			this.imported.put(path, host);
		}

		public void destory(String path) {
			Host host = this.imported.remove(path);
			if (host != null) {
				ZkContext.this.context.del(host);
			}
		}
	}

	private class ChangedRunnable implements Runnable {
		@Override
		public void run() {
			while (!ZkContext.this.shutdown.get()) {
				try {
					RouteChange change = ZkContext.this.changes.get();
					switch (change.action()) {
					case PUSH:
						ZkContext.this.importing(change.route());
						continue;
					case DEL:
						ZkContext.this.imported.destory(change.route());
						continue;
					}
				} catch (Throwable e) {
					e.printStackTrace();

					ZkContext.LOGGER.error(e.getMessage(), e);
				}
			}
			ZkContext.LOGGER.warn(this.getClass() + " shutdown ...");
		}
	}
}
