package com.icbc.wfs.service;

import java.io.InputStream;
import java.util.List;

public interface WfsGet {
	
	InputStream get(String path);
	List<String> getList(String path);
}
