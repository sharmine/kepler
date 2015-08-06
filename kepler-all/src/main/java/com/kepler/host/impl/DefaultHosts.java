package com.kepler.host.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.KeplerException;
import com.kepler.host.Host;
import com.kepler.host.HostCollector;
import com.kepler.host.HostLocks;
import com.kepler.host.Hosts;
import com.kepler.protocol.Request;
import com.kepler.router.Routing;

/**
 * @author kim 2015年7月9日
 */
public class DefaultHosts implements Hosts, HostCollector {

	private final static List<Host> EMPTY = Collections.unmodifiableList(new ArrayList<Host>());

	private final static Log LOGGER = LogFactory.getLog(DefaultHosts.class);

	private final CopyOnWriteArrayList<Host> hosts = new CopyOnWriteArrayList<Host>();

	private final HostLocks locks = new SegmentLocks();

	private final Tags tags = new Tags();

	private final Bans bans = new Bans();

	private final Routing routing;

	public DefaultHosts(Routing routing) {
		super();
		this.routing = routing;
	}

	public void put(Host host) {
		if (this.hosts.addIfAbsent(host)) {
			synchronized (this.locks.get(host)) {
				this.tags.put(host);
				this.bans.del(host);
			}
		}
	}

	public void del(Host host) {
		synchronized (this.locks.get(host)) {
			this.hosts.remove(host);
			this.tags.del(host);
			this.bans.del(host);
		}
	}

	public void ban(Host host) {
		synchronized (this.locks.get(host)) {
			if (this.hosts.remove(host) && this.tags.del(host)) {
				this.bans.put(host);
			}
		}
	}

	public void unban(Host host) {
		synchronized (this.locks.get(host)) {
			if (this.bans.del(host)) {
				this.tags.put(host);
				this.hosts.add(host);
			}
		}
	}

	public boolean contain(Host host) {
		return this.hosts.contains(host) || this.bans.contains(host);
	}

	public Host next(Request request) {
		return this.routing.route(request, this.validAll(request).validMain(request));
	}

	private DefaultHosts validMain(Request request) {
		if (this.main().isEmpty()) {
			DefaultHosts.LOGGER.warn("Main hosts is empty ...");
		}
		return this;
	}

	private DefaultHosts validAll(Request request) {
		if (this.all().isEmpty()) {
			throw new KeplerException("None service for " + request.service() + "(" + request.version() + ")");
		}
		return this;
	}

	public List<Host> all() {
		return this.hosts;
	}

	@Override
	public List<Host> main() {
		return this.tags.get(Host.TAG_DEFAULT);
	}

	@Override
	public List<Host> tags(String tag) {
		return this.tags.get(tag);
	}

	private class Tags {

		private final Map<String, List<Host>> tags = new ConcurrentHashMap<String, List<Host>>();

		public List<Host> get(String tag) {
			List<Host> hosts = this.tags.get(tag);
			return hosts != null ? hosts : DefaultHosts.EMPTY;
		}

		/**
		 * Put已由外部调用进行同步安全
		 * 
		 * @param host
		 * @return
		 */
		public Tags put(Host host) {
			List<Host> hosts = this.get(host.tag());
			(hosts = hosts != DefaultHosts.EMPTY ? hosts : new ArrayList<Host>()).add(host);
			this.tags.put(host.tag(), hosts);
			return this;
		}

		public boolean del(Host host) {
			return this.get(host.tag()).remove(host);
		}
	}

	private class Bans {

		private final CopyOnWriteArraySet<Host> bans = new CopyOnWriteArraySet<Host>();

		public void put(Host host) {
			this.bans.add(host);
			DefaultHosts.LOGGER.info("Host: " + host.getAsString() + " added ... ");
		}

		public boolean del(Host host) {
			boolean removed = this.bans.remove(host);
			if (removed) {
				DefaultHosts.LOGGER.info("Host: " + host.getAsString() + " removed ... ");
			}
			return removed;
		}

		public boolean contains(Host host) {
			return this.bans.contains(host);
		}
	}
}
