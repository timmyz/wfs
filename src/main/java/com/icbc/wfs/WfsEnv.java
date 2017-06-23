package com.icbc.wfs;

import com.icbc.dubbo.util.PropertyConfigurer;

public class WfsEnv {
	public static final String ROOT_DIR = getRootDir();
	public static final String GROUP = getGroup();
	public static final String GROUP_FLAG = getGroupFlag();

	private static String getRootDir() {
		String rootDir = System.getProperty("rootDir");
		if (rootDir == null) {
			rootDir = (String) PropertyConfigurer.getContextProperty("rootDir");
		}
		return rootDir;
	}

	private static String getGroup() {
		String group = System.getProperty("group");
		if (group == null) {
			group = (String) PropertyConfigurer.getContextProperty("group");
		}
		return group;
	}

	private static String getGroupFlag() {
		String group = getGroup();
		return group.substring(group.lastIndexOf("-") + 1);
	}

}
