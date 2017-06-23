package com.icbc.wfs;

import com.icbc.dubbo.util.PropertyConfigurer;

public class WfsEnv {
	public static final String ROOT_DIR = getRootDir();
	private static String getRootDir() {
		return (String) PropertyConfigurer.getContextProperty("rootDir");
	}
}
