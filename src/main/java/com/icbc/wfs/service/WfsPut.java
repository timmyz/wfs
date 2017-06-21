package com.icbc.wfs.service;

import java.io.InputStream;

public interface WfsPut {
	boolean put(String path, InputStream in);

	boolean del(String path);

	boolean put(String directory, String fileName);

	boolean del(String directory, String fileName);
}
