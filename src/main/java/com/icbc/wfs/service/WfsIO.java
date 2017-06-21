package com.icbc.wfs.service;

import java.io.InputStream;
import java.util.List;

public interface WfsIO {

	boolean put(String path, InputStream in);

	InputStream get(String path);

	boolean ren(String newPath, String oldPath);

	boolean del(String path);

	List<String> list(String path);
}
