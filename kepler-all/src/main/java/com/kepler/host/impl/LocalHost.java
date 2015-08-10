package com.kepler.host.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.KeplerException;
import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;

/**
 * @author kim 2015年7月8日
 */
public class LocalHost implements Host, Serializable {

	private final static long serialVersionUID = 1L;

	private final static Log LOGGER = LogFactory.getLog(LocalHost.class);

	private final static int PORT = Integer.valueOf(PropertiesUtils.get(LocalHost.class.getName().toLowerCase() + ".port", "9876"));

	private final static int RANGE = Integer.valueOf(PropertiesUtils.get(LocalHost.class.getName().toLowerCase() + ".range", "1000"));

	private final static int INTERVAL = Integer.valueOf(PropertiesUtils.get(LocalHost.class.getName().toLowerCase() + ".interval", "500"));

	private final static boolean STABLE = Boolean.valueOf(PropertiesUtils.get(LocalHost.class.getName().toLowerCase() + ".stable", "false"));

	private final Host local;

	public LocalHost() throws Exception {
		this.local = new DefaultHost(Host.GROUP, Host.TAG, this.address(), LocalHost.STABLE ? LocalHost.PORT : this.free());
	}

	private String address() throws Exception {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
					return address.getHostAddress();
				}
			}
		}
		LocalHost.LOGGER.warn("Using localhost mode for current service");
		return Host.LOOP;
	}

	private int free() throws Exception {
		for (int index = LocalHost.PORT; index < LocalHost.PORT + LocalHost.RANGE; index++) {
			try (Socket socket = new Socket()) {
				socket.connect(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), index), LocalHost.INTERVAL);
			} catch (IOException e) {
				LocalHost.LOGGER.debug("Port (" + index + ") used ... ");
				return index;
			}
		}
		throw new KeplerException("Cannot allocate port for current service");
	}

	@Override
	public int port() {
		return this.local.port();
	}

	@Override
	public String host() {
		return this.local.host();
	}

	@Override
	public String group() {
		return this.local.group();
	}

	@Override
	public String tag() {
		return this.local.tag();
	}

	public String getAsString() {
		return this.local.getAsString();
	}

	public boolean loop(Host host) {
		return this.local.loop(host);
	}

	public boolean loop(String host) {
		return this.local.loop(host);
	}

	public int hashCode() {
		return this.local.hashCode();
	}

	public boolean equals(Object ob) {
		return this.local.equals(ob);
	}
}
