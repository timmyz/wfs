package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsEdit;

@Service("wfsEditImpl")
public class WfsEditImpl implements WfsEdit {
	private static Logger logger = LoggerFactory.getLogger(WfsEditImpl.class);

	@Resource
	private WfsEdit wfsEdit;

	@Override
	public boolean del(String path) {
		File phyFile = WfsUtil.getPhyFile(path);
		if (phyFile.exists()) {
			return phyFile.delete();
		}
		return true;
	}

	public boolean put0(String path) {
		String directory = WfsUtil.getParent(path);
		RpcContext.getContext().setAttachment("routeKey", directory);
		String fileName = WfsUtil.getFileName(path);
		return wfsEdit.put(directory, fileName);
	}

	/*
	 * 创建虚拟目录
	 */
	@Override
	public boolean put(String dir, String fileName) {

		try {

			// 创建虚拟文件夹
			File vFolder = WfsUtil.getPhyFile(dir);
			if (!vFolder.exists()) {
				vFolder.mkdirs();
			}

			// 创建假文件，HASH路径+真文件名
			File vFile = new File(WfsUtil.getPhyFilePath(dir) + fileName);
			
			if (!vFile.exists()) {
				vFile.createNewFile();
			} else {
				return true;
			}

			// 如果虚拟路径不等于根路径，则创建父路
			if (!WfsUtil.ROOT.equals(dir)) {

				String prntDir = WfsUtil.getParent(dir);
				String grndPrntDir = WfsUtil.getParent(prntDir);
				String prntFolder = WfsUtil.getFileName(prntDir);

				// 按照父目录，调用
				RpcContext.getContext().setAttachment("routeKey", prntDir);
				return wfsEdit.put(grndPrntDir, prntFolder);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean del(String directory, String fileName) {
		File dirPhyFile = WfsUtil.getPhyFile(directory);
		if (dirPhyFile.exists() && dirPhyFile.isDirectory()) {
			for (File file : dirPhyFile.listFiles()) {
				if (file.getName().equals(fileName)) {
					while (file.exists()) {
						file.delete();
					}
					return true;
				}
			}
			logger.debug("no file matches the target filename in this directory");
		}
		return false;
	}

}
