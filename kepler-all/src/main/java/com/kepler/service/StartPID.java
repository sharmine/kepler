package com.kepler.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;

import com.kepler.config.PropertiesUtils;
import com.kepler.host.Host;

/**
 * @author kim 2015年7月15日
 */
public class StartPID {

	private final static String FILENAME = PropertiesUtils.get(StartPID.class.getName().toLowerCase() + ".file", "kepler") + "_{0}";

	private final static boolean REWRITE = Boolean.valueOf(PropertiesUtils.get(StartPID.class.getName().toLowerCase() + ".rewirte", "false"));

	private final File pid;

	private final PrintStream out;

	public StartPID(Host local) throws Exception {
		super();
		String file = MessageFormat.format(StartPID.FILENAME, String.valueOf(local.port()));
		this.out = new PrintStream(new FileOutputStream(file + ".out"));
		this.pid = new File(file + ".pid");
	}

	public void init() throws IOException {
		this.pid().pidOut();
	}

	private StartPID pid() throws IOException {
		try (FileWriter writer = new FileWriter(this.pid)) {
			writer.write(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		}
		return this;
	}

	private StartPID pidOut() {
		if (StartPID.REWRITE) {
			System.setOut(new PrintStream(this.out));
		}
		return this;
	}

	public void destory() {
		this.out.flush();
		this.out.close();
		this.pid.delete();
	}
}
