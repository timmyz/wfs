package com.icbc.wfs.service;

import java.io.InputStream;
import java.util.List;

import com.icbc.dubbo.router.BusiRouterFlag;

@BusiRouterFlag
public interface WfsGet {
	
	InputStream get(String path);
	List<String> getList(String path);
}
