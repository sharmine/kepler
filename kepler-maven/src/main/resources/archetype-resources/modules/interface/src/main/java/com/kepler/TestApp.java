package com.kepler;
import com.kepler.annotation.Version;

@Version("0.0.1-${artifactId}")
public interface TestApp {

	public String test(String kepler) throws Exception;
}
