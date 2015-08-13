package com.kepler.connection.impl;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;

import com.kepler.KeplerException;

/**
 * @author kim 2015年7月8日
 */
class DefaultChannelFactory<T extends Channel> implements ChannelFactory<T> {

	private final Class<? extends T> factory;

	public DefaultChannelFactory(Class<? extends T> factory) {
		super();
		this.factory = factory;
	}

	public T newChannel() {
		try {
			return this.factory.newInstance();
		} catch (Exception e) {
			throw new KeplerException(e);
		}
	}
}
