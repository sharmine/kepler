package com.kepler.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author kim 2015年7月16日
 */
final public class PropertiesUtils {

	private final static String FILE = System.getProperty("conf", "kepler.conf");

	private final static Log LOGGER = LogFactory.getLog(PropertiesUtils.class);

	private final static Properties PROPERTIES = new Properties();

	/**
	 * 已被使用的配置
	 */
	private final static Map<String, String> USED = new HashMap<String, String>();

	static {
		ClassPathResource resource = new ClassPathResource(PropertiesUtils.FILE);
		if (resource.exists()) {
			try (InputStream input = resource.getInputStream()) {
				PropertiesUtils.PROPERTIES.load(input);
				PropertiesUtils.LOGGER.info("Loading config: " + resource.getPath());
			} catch (Exception e) {
				e.printStackTrace();
				PropertiesUtils.LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public static String get(String key) {
		return PropertiesUtils.get(key, null);
	}

	/**
	 * System.getProperty -> Config File -> Def
	 * 
	 * @param key
	 * @param def
	 * @return
	 */
	public static String get(String key, String def) {
		String value = System.getProperty(key, PropertiesUtils.PROPERTIES.getProperty(key, def));
		PropertiesUtils.USED.put(key, value);
		return value;
	}

	public static Map<String, String> used() {
		return PropertiesUtils.USED;
	}
}
