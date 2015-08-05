package com.kepler.ack.impl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * @author kim 2015年7月10日
 */
public class Acks {

	/**
	 * 超时
	 */
	private final BlockingQueue<AckFuture> timeouts = new DelayQueue<AckFuture>();

	/**
	 * 等待
	 */
	private final Map<String, AckFuture> waitings = new ConcurrentHashMap<String, AckFuture>();

	public AckFuture put(AckFuture future) {
		// 首先保证加入超时检测队列
		if (this.timeouts.offer(future)) {
			this.waitings.put(future.request().ack(), future);
		}
		return future;
	}

	public AckFuture del(String ack) {
		AckFuture future = this.waitings.remove(ack);
		// 超时检测队列尚未被删除则表示Future允许正确返回
		return this.timeouts.remove(future) ? future : null;
	}

	public AckFuture timeout() throws InterruptedException {
		return this.timeouts.take();
	}
}
