package com.icbc.wfs.service;

import java.io.InputStream;

public interface WfsPut {
	boolean put(String path, InputStream in);
	boolean put(String path);
}
