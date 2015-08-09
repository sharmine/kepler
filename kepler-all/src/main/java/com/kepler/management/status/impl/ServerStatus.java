package com.kepler.management.status.impl;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.kepler.management.status.Status;

/**
 * @author kim 2015年8月8日
 */
public class ServerStatus implements Status {

	private final List<GarbageCollectorMXBean> gc = ManagementFactory.getGarbageCollectorMXBeans();

	private final OperatingSystemMXBean system = ManagementFactory.getOperatingSystemMXBean();

	private final RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();

	private final MemoryMXBean memory = ManagementFactory.getMemoryMXBean();

	private final MemoryUsage usage = this.memory.getHeapMemoryUsage();

	private final TreeMap<String, Object> fixed = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);

	public void init() {
		this.runtime().system();
	}

	private ServerStatus system() {
		this.fixed.put("S_ARCH", this.system.getArch());
		this.fixed.put("S_NAME", this.system.getName());
		this.fixed.put("S_VERSION", this.system.getVersion());
		this.fixed.put("S_AVAILABLE_PROCESSORS", this.system.getAvailableProcessors());
		return this;
	}

	private ServerStatus system(Map<String, Object> variable) {
		variable.put("S_LOAD_AVERAGE", this.system.getSystemLoadAverage());
		return this;
	}

	private ServerStatus runtime() {
		this.fixed.put("R_NAME", this.runtime.getName());
		this.fixed.put("R_VM_NAME", this.runtime.getVmName());
		this.fixed.put("R_VM_VERSION", this.runtime.getVmVersion());
		this.fixed.put("R_SPEC_VENDOR", this.runtime.getSpecVendor());
		this.fixed.put("R_SPEC_VERSION", this.runtime.getSpecVersion());
		return this;
	}

	private ServerStatus runtime(Map<String, Object> variable) {
		variable.put("R_UPTIME", this.runtime.getUptime());
		return this;
	}

	private ServerStatus memory(Map<String, Object> variable) {
		variable.put("M_USAGE_MAX", this.usage.getMax());
		variable.put("M_USAGE_USED", this.usage.getUsed());
		variable.put("M_USAGE_INIT", this.usage.getInit());
		variable.put("M_USAGE_COMMITTED", this.usage.getCommitted());
		return this;
	}

	private ServerStatus gc(Map<String, Object> variable) {
		for (GarbageCollectorMXBean each : this.gc) {
			variable.put("G_TIMES" + each.getName().toUpperCase(), each.getCollectionTime());
			variable.put("G_COUNT" + each.getName().toUpperCase(), each.getCollectionCount());
		}
		return this;
	}

	private Map<String, Object> variable(Map<String, Object> variable) {
		this.runtime(variable).system(variable).memory(variable).gc(variable);
		return variable;
	}

	@Override
	public Map<String, Object> get() {
		return this.variable(new TreeMap<String, Object>(this.fixed));
	}
}
