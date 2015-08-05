package com.kepler.invoker.impl;

import com.kepler.channel.ChannelContext;
import com.kepler.invoker.Invoker;
import com.kepler.protocol.Request;
import com.kepler.router.Router;

/**
 * @author kim 2015年7月8日
 */
public class ActualInvoker implements Invoker {

	private final ChannelContext channels;

	private final Router router;

	public ActualInvoker(ChannelContext channels, Router router) {
		super();
		this.channels = channels;
		this.router = router;
	}

	@Override
	public Object invoke(Request request) throws Exception {
		return this.channels.get(this.router.route(request)).invoke(request);
	}
}
