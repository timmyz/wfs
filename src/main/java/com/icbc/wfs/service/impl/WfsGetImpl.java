package com.icbc.wfs.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcException;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsGet;

//@Service(version = "0.0.1")
@Service("wfsGetImpl")
public class WfsGetImpl implements WfsGet {

	@Override
	public InputStream get(String path) {
		try {
			return new FileInputStream(WfsUtil.getPhyFile(path));
		} catch (FileNotFoundException e) {
			throw new RpcException();
		}
	}
}
