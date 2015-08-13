package com.kepler.management.status.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import com.kepler.management.status.Status;

/**
 * @author kim 2015年8月11日
 */
public class ThreadStatus implements Status {

	private final ThreadPoolExecutor threads;

	public ThreadStatus(ThreadPoolExecutor threads) {
		super();
		this.threads = threads;
	}

	@Override
	public Map<String, Object> get() {
		Map<String, Object> status = new HashMap<String, Object>();
		status.put("T_ACTIVE_COUNT", Thread.activeCount());
		status.put("T_POOL_SIZE", this.threads.getPoolSize());
		status.put("T_POOL_TASK_COUNT", this.threads.getTaskCount());
		status.put("T_POOL_ACTIVE_COUNT", this.threads.getActiveCount());
		status.put("T_POOL_CORE_POOL_SIZE", this.threads.getCorePoolSize());
		status.put("T_POOL_LARGEST_POOL_SIZE", this.threads.getLargestPoolSize());
		status.put("T_POOL_MAXIMUM_POOL_SIZE", this.threads.getMaximumPoolSize());
		status.put("T_POOL_COMPLETED_TASK_COUNT", this.threads.getCompletedTaskCount());
		return status;
	}
}
