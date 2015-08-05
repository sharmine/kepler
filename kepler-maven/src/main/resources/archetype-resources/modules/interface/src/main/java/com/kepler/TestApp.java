package com.kepler;
import com.kepler.service.annotation.Version;

@Version("0.0.1-SNAPSHOT")
public interface TestApp {

	public String test(String kepler) throws Exception;
}
