package com.icbc.wfs.service;

import java.io.InputStream;
import java.util.List;

import com.icbc.dubbo.router.HashRouterFlag;

@HashRouterFlag
public interface WfsGet {
	
	InputStream get(String path);
	InputStream getPhy(String path);
	List<String> getList(String path);
	List<String> getPhyList(String path);
}
