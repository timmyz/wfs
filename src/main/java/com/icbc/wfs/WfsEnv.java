package com.icbc.wfs;

import com.icbc.dubbo.util.PropertyConfigurer;

public class WfsEnv {
	public static final String ROOT_DIR = getRootDir();
	public static final String GROUP = getGroup();
	public static final String GROUP_FLAG = getGroupFlag();

	private static String getRootDir() {
		return (String) PropertyConfigurer.getContextProperty("rootDir");
	}

	private static String getGroup() {
		return (String) PropertyConfigurer.getContextProperty("dubbo.service.group");
	}

	private static String getGroupFlag() {
		String group = (String) PropertyConfigurer.getContextProperty("dubbo.service.group");
		return group.substring(group.lastIndexOf("-") + 1);
	}

}
