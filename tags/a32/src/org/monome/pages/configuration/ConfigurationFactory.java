package org.monome.pages.configuration;

public class ConfigurationFactory {
	
	static Configuration instance;
	
	public static Configuration getConfiguration() {
		return instance;
	}
	
	public static void setConfiguration(Configuration config) {
		instance = config;
	}

}
