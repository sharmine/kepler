package com.kepler.host.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.kepler.config.PropertiesUtils;
import com.kepler.conncetion.Connects;
import com.kepler.extension.Extension;
import com.kepler.host.Host;
import com.kepler.host.Hosts;
import com.kepler.host.HostsContext;
import com.kepler.protocol.Request;
import com.kepler.router.Router;
import com.kepler.router.Routing;
import com.kepler.router.routing.TagRouting;
import com.kepler.service.impl.Service;

/**
 * @author kim 2015年7月9日
 */
public class DefaultContext implements HostsContext, Extension, Router {

	private final static String ROUTING = PropertiesUtils.get(DefaultHosts.class.getName().toLowerCase(), TagRouting.NAME);

	/**
	 * 服务/版本 - 主机映射
	 */
	private final Map<Service, Hosts> hosts = new ConcurrentHashMap<Service, Hosts>();

	/**
	 * 路由策略
	 */
	private final Map<String, Routing> routes = new ConcurrentHashMap<String, Routing>();

	/**
	 * 重连
	 */
	private final Connects connects;

	public DefaultContext(Connects connects) throws Exception {
		super();
		this.connects = connects;
	}

	private Hosts put(Service version, Hosts hosts) {
		this.hosts.put(version, hosts);
		return hosts;
	}

	// 对于首次Get的Vs进行默认创建
	public Hosts get(Class<?> service, String version) {
		Service vs = new Service(service, version);
		Hosts hosts = this.hosts.get(vs);
		return hosts != null ? hosts : this.put(vs, new DefaultHosts(this.routes.get(DefaultContext.ROUTING)));
	}

	public Collection<Hosts> hosts() {
		return this.hosts.values();
	}

	@Override
	public void ban(Host host) {
		boolean connected = false;
		for (Hosts each : this.hosts()) {
			each.ban(host);
			// 当Hosts尚存Host引用时(含Ban)尝试重连
			connected = connected || each.contain(host);
		}
		// 当Connects中存在待触发重连时, 服务下线(HostsContext.del). 则Connects重连由于无法建立连接而再次调用Ban进入死循环.
		if (connected) {
			// 在尝试重连充分必要条件为任意Hosts尚存Host映射.即使重连失败时亦能在下次尝试时进行终止
			this.connects.put(host);
		}
	}

	public void del(Host host) {
		for (Hosts each : this.hosts.values()) {
			each.del(host);
		}
	}

	public void unban(Host host) {
		for (Hosts each : this.hosts()) {
			each.unban(host);
		}
	}

	@Override
	public Host route(Request request) {
		return this.get(request.service(), request.version()).next(request);
	}

	@Override
	public DefaultContext install(Object instance) {
		Routing routing = Routing.class.cast(instance);
		this.routes.put(routing.name(), routing);
		return this;
	}

	@Override
	public Class<?> interested() {
		return Routing.class;
	}
}
