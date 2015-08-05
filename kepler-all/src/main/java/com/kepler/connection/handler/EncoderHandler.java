package com.kepler.connection.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import com.kepler.serial.SerialFactory;

/**
 * @author kim 2015年7月8日
 */
@Sharable
public class EncoderHandler extends ChannelInboundHandlerAdapter {
	
	private final SerialFactory serial;

	private final Class<?> clazz;

	public EncoderHandler(SerialFactory serial, Class<?> clazz) {
		super();
		this.serial = serial;
		this.clazz = clazz;
	}

	public void channelRead(final ChannelHandlerContext ctx, Object message) throws Exception {
		ctx.fireChannelRead(this.serial.serial(this.serial(ByteBuf.class.cast(message)), CodecHeader.DEFAULT, this.clazz));
	}

	public byte[] serial(ByteBuf buffer) {
		try {
			byte[] bytes = new byte[buffer.readableBytes()];
			buffer.readBytes(bytes);
			return bytes;
		} finally {
			ReferenceCountUtil.release(buffer);
		}
	}
}
