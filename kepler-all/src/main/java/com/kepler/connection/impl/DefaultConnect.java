package com.kepler.connection.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kepler.ack.impl.AckFuture;
import com.kepler.ack.impl.Acks;
import com.kepler.channel.ChannelContext;
import com.kepler.channel.ChannelInvoker;
import com.kepler.collector.rpc.Collector;
import com.kepler.config.PropertiesUtils;
import com.kepler.conncetion.Closeable;
import com.kepler.conncetion.Connect;
import com.kepler.conncetion.Connects;
import com.kepler.connection.handler.CodecHeader;
import com.kepler.connection.handler.DecoderHandler;
import com.kepler.connection.handler.EncoderHandler;
import com.kepler.host.Host;
import com.kepler.host.HostLocks;
import com.kepler.host.impl.SegmentLocks;
import com.kepler.invoker.Invoker;
import com.kepler.protocol.Request;
import com.kepler.protocol.Response;
import com.kepler.serial.SerialFactory;

/**
 * Client 2 Service Connection
 * 
 * @author kim 2015年7月10日
 */
public class DefaultConnect implements Connect {

	/**
	 * 黏包最大长度
	 */
	private final static int MAX_FRAME_LENGTH = Integer.valueOf(PropertiesUtils.get(LengthFieldBasedFrameDecoder.class.getName().toLowerCase() + ".max_frame_length", String.valueOf(Integer.MAX_VALUE)));

	/**
	 * 监听待重连线程数量
	 */
	private final static int ESTABLISH_THREAD = Integer.valueOf(PropertiesUtils.get(DefaultConnect.class.getName().toLowerCase() + ".establish_thread", "2"));

	private final static ChannelFactory<SocketChannel> FACTORY = new DefaultChannelFactory<SocketChannel>(NioSocketChannel.class);

	private final static Log LOGGER = LogFactory.getLog(DefaultConnect.class);

	private final InitializerFactory inits = new InitializerFactory();

	private final AtomicBoolean shutdown = new AtomicBoolean();

	/**
	 * 建立连接任务,无状态
	 */
	private final Runnable establish = new EstablishRunnable();

	private final HostLocks locks = new SegmentLocks();

	private final Acks acks;

	private final Host local;

	private final Connects connects;

	private final Closeable closeable;

	private final Collector collector;

	private final SerialFactory serial;

	private final ChannelContext channels;

	private final ThreadPoolExecutor threads;

	public DefaultConnect(Acks acks, Host local, Connects connects, Closeable closeable, ChannelContext channels, SerialFactory serial, Collector collector, ThreadPoolExecutor threads) {
		super();
		this.acks = acks;
		this.local = local;
		this.serial = serial;
		this.threads = threads;
		this.connects = connects;
		this.channels = channels;
		this.closeable = closeable;
		this.collector = collector;
	}

	public void init() {
		for (int index = 0; index < DefaultConnect.ESTABLISH_THREAD; index++) {
			this.threads.execute(this.establish);
		}
		// 黏包
		this.inits.add(new LengthFieldPrepender(CodecHeader.DEFAULT));
		this.inits.add(new DecoderHandler(DefaultConnect.this.serial));
		this.inits.add(new EncoderHandler(DefaultConnect.this.serial, Response.class));
	}

	public void destory() {
		this.shutdown.set(true);
	}

	public void connect(Host host) throws Exception {
		// 1个Host仅允许建立1个连接
		synchronized (this.locks.get(host)) {
			if (!this.channels.contain(host)) {
				this.connect(new DefaultChannelInvoker(new InvokerHandler(host), new Bootstrap(), host));
			}
		}
	}

	private void connect(DefaultChannelInvoker invoker) throws Exception {
		try {
			// 同步等待
			invoker.bootstrap().group(new NioEventLoopGroup()).channelFactory(DefaultConnect.FACTORY).handler(DefaultConnect.this.inits.factory(invoker.invoker())).remoteAddress(new InetSocketAddress(invoker.host().loop(this.local) ? Host.LOOP : invoker.host().host(), invoker.host().port())).connect().sync();
			// 加入通道
			this.channels.put(invoker.host(), invoker);
		} catch (Throwable e) {
			// 关闭并重连
			this.closeable.close(invoker.host());
			throw e;
		}
	}

	private class DefaultChannelInvoker implements ChannelInvoker {

		private final InvokerHandler invoker;

		private final Bootstrap bootstrap;

		private final Host host;

		private DefaultChannelInvoker(InvokerHandler invoker, Bootstrap bootstrap, Host host) {
			super();
			this.bootstrap = bootstrap;
			this.invoker = invoker;
			this.host = host;
		}

		public Host host() {
			return this.host;
		}

		public Bootstrap bootstrap() {
			return this.bootstrap;
		}

		public InvokerHandler invoker() {
			return this.invoker;
		}

		// closeable.close间接调用
		public void close() {
			// 禁止在EventLoop线程进行Boostrap关闭, 将造成死锁(当前线程也为EventLoop线程, 无法关闭)
			DefaultConnect.this.threads.execute(new CloseRunnable(this.bootstrap, this.host));
		}

		public Object invoke(Request request) throws Exception {
			return this.invoker.invoke(request);
		}
	}

	// 非共享
	private class InvokerHandler extends ChannelInboundHandlerAdapter implements Invoker {

		private final Host host;

		private ChannelHandlerContext ctx;

		public InvokerHandler(Host host) {
			super();
			this.host = host;
		}

		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			ctx.fireChannelRegistered();
			this.ctx = ctx;
			DefaultConnect.LOGGER.warn("Connect binding (" + DefaultConnect.this.local.getAsString() + " to " + this.host.getAsString() + ") ...");
		}

		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			DefaultConnect.LOGGER.warn("Connect closing (" + DefaultConnect.this.local.getAsString() + " to " + this.host.getAsString() + ") ...");
			DefaultConnect.this.closeable.close(this.host);
			ctx.fireChannelUnregistered();
		}

		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
			this.channelUnregistered(ctx);
		}

		public Object invoke(Request request) {
			return this.invoke(new AckFuture(this.host, request));
		}

		private Object invoke(AckFuture future) {
			// 加入ACK池 -> 发送消息 -> 等待ACK
			try {
				this.ctx.writeAndFlush(DefaultConnect.this.acks.put(future).request()).addListener(ExceptionListener.TRACE);
				return future.get();
			} finally {
				DefaultConnect.this.collector.collect(future);
			}
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
			Response response = Response.class.cast(message);
			AckFuture ack = DefaultConnect.this.acks.del(response.ack());
			// 如获取不到ACK表示已超时
			if (ack != null) {
				ack.response(response);
			} else {
				DefaultConnect.LOGGER.warn("Missing future for ack: " + response.ack() + " (" + this.host.getAsString() + "), may be timeout ...");
			}
		}
	}

	private class InitializerFactory {

		private List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		public void add(ChannelHandler handler) {
			this.handlers.add(handler);
		}

		public ChannelInitializer<SocketChannel> factory(final InvokerHandler handler) {
			return new ChannelInitializer<SocketChannel>() {
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(DefaultConnect.MAX_FRAME_LENGTH, 0, CodecHeader.DEFAULT));
					for (ChannelHandler each : InitializerFactory.this.handlers) {
						channel.pipeline().addLast(each);
					}
					channel.pipeline().addLast(handler);
				}
			};
		}
	}

	private class CloseRunnable implements Runnable {

		private final Bootstrap bootstrap;

		private final Host host;

		public CloseRunnable(Bootstrap bootstrap, Host host) {
			super();
			this.bootstrap = bootstrap;
			this.host = host;
		}

		@Override
		public void run() {
			try {
				this.bootstrap.group().shutdownGracefully().sync();
				DefaultConnect.LOGGER.warn("Host (Physical): " + this.host.getAsString() + " closed ... ");
			} catch (Exception e) {
				e.printStackTrace();
				DefaultConnect.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 重连线程
	 * 
	 * @author kim 2015年7月20日
	 */
	private class EstablishRunnable implements Runnable {
		@Override
		public void run() {
			while (!DefaultConnect.this.shutdown.get()) {
				try {
					DefaultConnect.this.connect(DefaultConnect.this.connects.get());
				} catch (Throwable e) {
					DefaultConnect.LOGGER.error(e.getMessage(), e);
				}
			}
			DefaultConnect.LOGGER.warn(this.getClass() + " shutdown ...");
		}
	}
}
