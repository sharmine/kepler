package com.kepler.router.routing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.kepler.host.Host;
import com.kepler.host.HostCollector;
import com.kepler.protocol.Request;
import com.kepler.router.Routing;

/**
 * @author kim 2015年7月15日
 */
public class TagRouting implements Routing {

	public final static String NAME = "tag";

	/**
	 * 指定Tag时共享基数,非均衡分布
	 */
	private final AtomicInteger index = new AtomicInteger();

	@Override
	public Host route(Request request, HostCollector collect) {
		// 获取含指定Tag的主机集合
		List<Host> hosts = collect.tags(request.headers().get(Host.TAG_KEY, Host.TAG_DEFAULT));
		// 如果Hosts不存在则使用Main集合主机, 如果Main主机集合不存在则抛出By zero exception
		return (hosts = (!hosts.isEmpty() ? hosts : collect.main())).get((this.index.incrementAndGet() & Byte.MAX_VALUE) % hosts.size());
	}

	@Override
	public String name() {
		return TagRouting.NAME;
	}
}
