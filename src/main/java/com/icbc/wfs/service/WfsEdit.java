package com.icbc.wfs.service;

public interface WfsEdit {

	boolean del(String path);

	boolean put(String directory, String fileName);

	boolean del(String directory, String fileName);
}
