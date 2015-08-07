package com.kepler.collector.rpc.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.kepler.ack.Ack;
import com.kepler.collector.rpc.Collector;
import com.kepler.collector.rpc.Conditions;
import com.kepler.org.apache.commons.collections.map.MultiKeyMap;
import com.kepler.service.Imported;

/**
 * @author kim 2015年7月22日
 */
public class ImportedCollector implements Runnable, Collector, Imported {

	/**
	 * 当前, 切换, 清理
	 */
	private MultiKeyMap[] conditions = new MultiKeyMap[] { new MultiKeyMap(), new MultiKeyMap(), new MultiKeyMap() };

	/**
	 * 当前分钟
	 */
	private final AtomicLong minute = new AtomicLong(this.minute());

	/**
	 * Start from 1
	 */
	private final AtomicInteger index = new AtomicInteger(1);

	private final ThreadPoolExecutor threads;

	public ImportedCollector(ThreadPoolExecutor threads) {
		super();
		this.threads = threads;
	}

	@Override
	public void collect(Ack ack) {
		// Service,Version,Method维度
		DefaultConditions.class.cast(this.curr().get(ack.request().service().getName(), ack.request().version(), ack.request().method())).put(ack.local(), ack.host(), ack.status(), ack.elapse());
	}

	@Override
	public void subscribe(Class<?> service, String version) throws Exception {
		for (int index = 0; index < this.conditions.length; index++) {
			this.methods(service, version, index);
		}
	}

	private void methods(Class<?> service, String version, int index) {
		for (Method method : service.getMethods()) {
			this.conditions[index].put(service.getName(), version, method.getName(), new DefaultConditions(service.getName(), version, method.getName()));
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<Conditions> conditions() {
		// 同分钟内重复使用当前计数器, 否则切换
		return this.minute() == this.minute.get() ? this.curr().values() : this.exchange().values();
	}

	private long minute() {
		return TimeUnit.MINUTES.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	private MultiKeyMap prev() {
		return this.index(-1);
	}

	private MultiKeyMap curr() {
		return this.index(0);
	}

	private MultiKeyMap next() {
		return this.index(1);
	}

	private MultiKeyMap index(int index) {
		return this.conditions[((this.index.get() + index) & Byte.MAX_VALUE) % this.conditions.length];
	}

	/**
	 * 1, Index++, 2, Set Minute, 3, Prepare clear
	 * 
	 * @return
	 */
	private MultiKeyMap exchange() {
		this.index.incrementAndGet();
		this.minute.set(this.minute());
		this.threads.execute(this);
		return this.prev();
	}

	public void run() {
		for (Object each : ImportedCollector.this.next().values()) {
			DefaultConditions.class.cast(each).clear();
		}
	}
}
