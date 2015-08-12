package com.kepler.main.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;

import com.kepler.config.PropertiesUtils;
import com.kepler.main.Pid;

/**
 * @author kim 2015年7月15日
 */
public class StartPID implements Pid{

	private final static String FILENAME = PropertiesUtils.get(StartPID.class.getName().toLowerCase() + ".file", "kepler") + "_{0}";

	private final File file;

	private final String pid;

	public StartPID() throws Exception {
		super();
		this.pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		this.file = new File(MessageFormat.format(StartPID.FILENAME, this.pid) + ".pid");
	}

	public void init() throws IOException {
		try (Writer writer = new FileWriter(this.file)) {
			writer.write(this.pid);
		}
	}

	public void destory() {
		this.file.delete();
	}

	public String pid() {
		return this.pid;
	}
}
