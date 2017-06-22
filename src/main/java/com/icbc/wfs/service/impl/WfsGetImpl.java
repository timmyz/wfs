package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

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

	/**
	 * 物理层实现ls命令，获取文件列表
	 */
	@Override
	public List<String> getList(String path) {

		List<String> fileList = new LinkedList<String>();

		File dir = WfsUtil.getPhyFile(path);

		if (!dir.exists() && !dir.isDirectory()) {
			return null;
		}

		File[] fileArray = dir.listFiles();

		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isFile()) {

				fileList.add(fileArray[i].getName());
			}
		}

		return null;
	}
}