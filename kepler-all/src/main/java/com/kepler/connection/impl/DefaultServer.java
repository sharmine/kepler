package com.kepler.connection.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.config.PropertiesUtils;
import com.kepler.connection.handler.CodecHeader;
import com.kepler.connection.handler.DecoderHandler;
import com.kepler.connection.handler.EncoderHandler;
import com.kepler.header.HeadersContext;
import com.kepler.host.Host;
import com.kepler.protocol.Request;
import com.kepler.protocol.Response;
import com.kepler.protocol.ResponseFactory;
import com.kepler.protocol.impl.DefaultResponseFactory;
import com.kepler.serial.SerialFactory;
import com.kepler.service.ExportedContext;

/**
 * @author kim 2015年7月8日
 */
public class DefaultServer {

	/**
	 * 黏包最大长度
	 */
	private final static int MAX_FRAME_LENGTH = Integer.valueOf(PropertiesUtils.get(LengthFieldBasedFrameDecoder.class.getName().toLowerCase() + ".max_frame_length", String.valueOf(Integer.MAX_VALUE)));

	private final static int TIMEOUT = Integer.valueOf(PropertiesUtils.get(DefaultServer.class.getName().toLowerCase() + ".timeout", String.valueOf(Integer.MAX_VALUE)));

	private final static DefaultChannelFactory<ServerChannel> FACTORY = new DefaultChannelFactory<ServerChannel>(NioServerSocketChannel.class);

	private final static ResponseFactory RESPONSE = new DefaultResponseFactory();

	private final static Log LOGGER = LogFactory.getLog(DefaultServer.class);

	private final InitializerFactory inits = new InitializerFactory();

	private final ServerBootstrap bootstrap = new ServerBootstrap();

	private final ThreadPoolExecutor threads;

	private final ExportedContext exported;

	private final ResponseFactory response;

	private final HeadersContext headers;

	private final SerialFactory serial;

	private final Host local;

	public DefaultServer(Host local, ExportedContext exported, SerialFactory serial, HeadersContext headers, ThreadPoolExecutor threads) {
		this(local, exported, serial, DefaultServer.RESPONSE, headers, threads);
	}

	public DefaultServer(Host local, ExportedContext exported, SerialFactory serial, ResponseFactory response, HeadersContext headers, ThreadPoolExecutor threads) {
		super();
		this.exported = exported;
		this.response = response;
		this.threads = threads;
		this.headers = headers;
		this.serial = serial;
		this.local = local;
	}

	public void init() throws Exception {
		this.inits.add(new LengthFieldPrepender(CodecHeader.DEFAULT));
		this.inits.add(new DecoderHandler(DefaultServer.this.serial));
		this.inits.add(new EncoderHandler(DefaultServer.this.serial, Request.class));
		this.inits.add(new ExportedHandler());
		// 同步
		this.bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channelFactory(DefaultServer.FACTORY).childHandler(this.inits.factory()).option(ChannelOption.SO_REUSEADDR, true).bind(this.local.port()).sync();
	}

	public void destory() throws Exception {
		this.bootstrap.group().shutdownGracefully().sync();
		DefaultServer.LOGGER.warn("Server shutdown ... ");
	}

	private class InitializerFactory {

		private List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		public void add(ChannelHandler handler) {
			this.handlers.add(handler);
		}

		public ChannelInitializer<SocketChannel> factory() {
			return new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(DefaultServer.MAX_FRAME_LENGTH, 0, CodecHeader.DEFAULT));
					for (ChannelHandler each : InitializerFactory.this.handlers) {
						channel.pipeline().addLast(each);
					}
				}
			};
		}
	}

	@Sharable
	private class ExportedHandler extends ChannelInboundHandlerAdapter {

		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			DefaultServer.LOGGER.warn("Connect binding (" + ctx.channel().localAddress() + " to " + ctx.channel().remoteAddress() + ") ...");
			ctx.fireChannelActive();
		}

		/**
		 * @see com.kepler.host.LocalHost 会触发对本地服务端口嗅探
		 */
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			DefaultServer.LOGGER.error("Client closing (" + ctx.channel().localAddress() + " to " + ctx.channel().remoteAddress() + ") ...");
			ctx.close().addListener(ExceptionListener.TRACE);
			ctx.fireChannelInactive();
		}

		// 任何未捕获异常(如OOM)均需要终止通道
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			this.channelUnregistered(ctx);
		}

		@Override
		public void channelRead(final ChannelHandlerContext ctx, Object message) throws Exception {
			DefaultServer.this.threads.execute(new Reply(ctx, Request.class.cast(message)));
		}

		private class Reply implements Runnable {

			private final ChannelHandlerContext ctx;

			private final Request request;

			private long start;

			public Reply(ChannelHandlerContext ctx, Request request) {
				super();
				this.ctx = ctx;
				this.request = request;
			}

			private Reply init() {
				this.start = System.currentTimeMillis();
				DefaultServer.this.headers.put(this.request.headers());
				return this;
			}

			@Override
			public void run() {
				this.init().ctx.writeAndFlush(this.response(this.request)).addListener(ExceptionListener.TRACE);
			}

			/**
			 * Response禁止抛出业务异常
			 * 
			 * @param request
			 * @return
			 */
			private Response response(Request request) {
				try {
					// 获取服务并执行
					return DefaultServer.this.response.response(request.ack(), DefaultServer.this.exported.get(request.service(), request.version()).invoke(request));
				} catch (Throwable e) {
					// 业务异常
					e.printStackTrace();
					return DefaultServer.this.response.throwable(request.ack(), e);
				} finally {
					this.elapse();
				}
			}

			private void elapse() {
				long elapse = System.currentTimeMillis() - this.start;
				if (DefaultServer.TIMEOUT < elapse) {
					DefaultServer.LOGGER.warn("ACK: " + this.request.ack() + " (" + elapse + " / " + DefaultServer.TIMEOUT + ") may be timeout ... ");
				}
			}
		}
	}
}
