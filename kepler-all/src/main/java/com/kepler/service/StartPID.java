package com.kepler.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;

import com.kepler.config.PropertiesUtils;

/**
 * @author kim 2015年7月15日
 */
public class StartPID {

	private final static String FILENAME = PropertiesUtils.get(StartPID.class.getName().toLowerCase() + ".file", "kepler") + "_{0}";

	private final File file;

	private final String pid;

	public StartPID() throws Exception {
		super();
		this.pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		this.file = new File(MessageFormat.format(StartPID.FILENAME, this.pid) + ".pid");
	}

	public void init() throws IOException {
		this.pid(this.pid);
	}

	private StartPID pid(String pid) throws IOException {
		try (FileWriter writer = new FileWriter(this.file)) {
			writer.write(pid);
		}
		return this;
	}

	public void destory() {
		this.file.delete();
	}
}
