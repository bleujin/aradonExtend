package net.ion.im.bbs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class URLConfigLoader {

	private String configFileName;
	private URLConfig config;
	
	public URLConfigLoader(String configFileName) throws IOException {
		this.configFileName = configFileName;
		init();
	}
	
	public void init() throws IOException {
		Properties properties = new Properties();
		InputStream in = null; 
		try {
			in = new FileInputStream(configFileName);
			properties.load(in);
			
			config = new URLConfig(properties);
			
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if(in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	public URLConfig getConfig() {
		return config;
	}
}
