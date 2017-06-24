package com.icbc.wfs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.icbc.dubbo.util.PropertyConfigurer;

public class WfsEnv {
	public static final String ROOT_DIR = getRootDir();
	public static final String GROUP = getGroup();
	public static final List<String> HashFolders = getHashFolders();
	public static final String GROUP_FLAG = getGroupFlag();
	public static final int PHY_DIR_LENGTH = 2;

	private static String getRootDir() {
		String rootDir = System.getProperty("rootDir");
		if (rootDir == null) {
			rootDir = (String) PropertyConfigurer.getContextProperty("rootDir");
		}
		return rootDir + (rootDir.endsWith(File.separator) ? "" : File.separator);
	}

	private static String getGroup() {
		String group = System.getProperty("group");
		if (group == null) {
			group = (String) PropertyConfigurer.getContextProperty("group");
		}
		return group;
	}

	private static List<String> getHashFolders() {

		List<String> hashFolders = new LinkedList<String>();

		String groupArr[] = WfsEnv.GROUP.split("-");

		Integer l = groupArr.length - 1;

		while (1 < l) {
			long beginInt = WfsUtil.nParseLong(groupArr[--l]);
			long endInt = WfsUtil.nParseLong(groupArr[--l]);
			for (long i = beginInt; i < endInt; i++) {
				hashFolders.add(String.format("%" + PHY_DIR_LENGTH + "s", Long.toHexString(-i)).replace(' ', '0'));
			}
		}

		return hashFolders;
	}

	private static String getGroupFlag() {
		String group = getGroup();
		return group.substring(group.lastIndexOf("-") + 1);
	}
}
