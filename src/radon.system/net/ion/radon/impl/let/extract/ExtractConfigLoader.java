package net.ion.radon.impl.let.extract;

public class ExtractConfigLoader {

	private String configPath;
	private String exePath;
	
	public ExtractConfigLoader(String configPath, String exePath) {
		this.configPath = configPath;
		this.exePath = exePath;
	}
	
	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getExePath() {
		return exePath;
	}

	public void setExePath(String exePath) {
		this.exePath = exePath;
	}
	
}
