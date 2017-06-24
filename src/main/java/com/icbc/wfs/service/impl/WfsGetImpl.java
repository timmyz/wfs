package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcException;
import com.icbc.wfs.WfsEnv;
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
		File dir = WfsUtil.getPhyFile(path);
		if (!dir.exists() && !dir.isDirectory()) {
			throw new RpcException();
		}
		List<String> fileList = new LinkedList<String>();
		File[] fileArray = dir.listFiles();
		for (int i = 0; i < fileArray.length; i++) {
			fileList.add(fileArray[i].getName());
		}
		return fileList;
	}

	/*
	 * 递归获取目录下所有文件及文件夹
	 */
	public static List<String> getFileListRcrsv(String folder) {

		File file = new File(WfsEnv.ROOT_DIR + WfsUtil.PATH_SEPARATOR + folder);
		List<String> fileList = new LinkedList<String>();
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] fileArr = file.listFiles();
				if (fileArr == null) {
					return fileList;
				}
				for (int i = 0; i < fileArr.length; i++) {

					File fileOne = fileArr[i];

					if (fileOne.isFile()) {
						String str = "0";
						if (fileOne.length() > 0) {
							str = "1";
						}
						fileList.add(str + ":" + fileOne.getPath().substring(WfsEnv.ROOT_DIR.length()));

					} else if (fileOne.isDirectory()) {
						fileList.add("2:" + fileOne.getPath().substring(WfsEnv.ROOT_DIR.length()));
						fileList.addAll(getFileListRcrsv(fileOne.getPath()));

					}
				}
				return fileList;
			} else if (file.isFile()) {
				fileList.add(file.getPath());
				return fileList;
			}
		} else {
			return null;
		}
		return fileList;
	}

	@Override
	public InputStream getPhy(String path) {
		// TODO Auto-generated method stub
		return null;
	}

}
