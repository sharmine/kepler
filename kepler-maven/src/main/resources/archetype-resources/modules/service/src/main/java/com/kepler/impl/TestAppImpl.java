package com.kepler.impl;

import com.kepler.TestApp;
import com.kepler.annotation.Service;

@Service(version = "0.0.1-${artifactId}", autowired = true)
public class TestAppImpl implements TestApp {

	public String test(String kepler) throws Exception {
		return "Hello world " + kepler;
	}
}
