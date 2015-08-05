package com.kepler.connection.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import com.kepler.connection.impl.ExceptionListener;
import com.kepler.serial.SerialFactory;

/**
 * @author kim 2015年7月8日
 */
@Sharable
public class DecoderHandler extends ChannelOutboundHandlerAdapter {

	private final SerialFactory serial;

	public DecoderHandler(SerialFactory serial) {
		super();
		this.serial = serial;
	}

	public void write(ChannelHandlerContext ctx, final Object msg, ChannelPromise promise) throws Exception {
		ctx.writeAndFlush(Unpooled.wrappedBuffer(this.serial.serial(msg))).addListener(ExceptionListener.TRACE);
	}
}
