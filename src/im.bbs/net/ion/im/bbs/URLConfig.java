package net.ion.im.bbs;

import java.util.Properties;

public class URLConfig {

	private Properties properties;
	
	public URLConfig(Properties properties) {
		this.properties = properties;
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}
	
	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
