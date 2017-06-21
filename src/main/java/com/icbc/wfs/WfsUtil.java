package com.icbc.wfs;

public class WfsUtil {
	public static final String PATH_SEPARATOR = "/";
	public static final String ROOT = PATH_SEPARATOR;

	public static String getFileName(String path) {
		if (ROOT.equals(path)) {
			return ROOT;
		} else {
			return path.substring(path.lastIndexOf(PATH_SEPARATOR) + 1);
		}
	}

	public static String getParent(String path) {
		if (ROOT.equals(path)) {
			return ROOT;
		} else {
			return path.substring(0, path.lastIndexOf(PATH_SEPARATOR));
		}
	}

}
