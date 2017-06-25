package com.icbc.wfs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.constant.FileType;
import com.icbc.dubbo.router.HashRouter;
import com.icbc.dubbo.util.SpringContextHolder;
import com.icbc.wfs.service.WfsGet;

public class WfsRestorer {
	public static final long DELAY_TIME = 8000;
	
	private static Logger logger = LoggerFactory.getLogger(WfsRestorer.class);
	private static boolean duringRestore = false;

	public static boolean isDuringRestore() {
		return duringRestore;
	}

	public static void setDuringRestore(boolean duringRestore) {
		WfsRestorer.duringRestore = duringRestore;
	}

	public static void doRestore() {

		String rootDir = System.getProperty("duringRestore");
		if (rootDir != null) {
			return;
		}
		duringRestore = true;

		logger.info("prepare restore");
		try {
			Thread.sleep(DELAY_TIME);
		} catch (InterruptedException e) {
			logger.info("restore sleep error");
		}
		logger.info("do restore");

		WfsGet wfsGet = (WfsGet) SpringContextHolder.getApplicationContext().getBean("wfsGet", WfsGet.class);

		for (String folder : WfsEnv.HashFolders) {

			RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, folder);
			RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, WfsEnv.GROUP_FLAG);
			List<String> fileList = new LinkedList<String>();

			try {
				fileList = wfsGet.getPhyList(folder);
			} catch (NoSuchElementException e) {
				logger.info("doRestore-->NoSuchElementException:" + folder);
				continue;
			}

			List<String> phyFileList = new LinkedList<String>();

			for (String fileInfo : fileList) {
				String[] fileInfoArr = fileInfo.split(":");
				// 文件类型
				String fileType = fileInfoArr[0];
				// 远程路径
				String filePath = fileInfoArr[1];

				// 本地物理路径=根目录+哈希
				String localPath = WfsEnv.ROOT_DIR + folder + File.separator + filePath;

				if (fileType.equals(FileType.Directory)) {
					// 目录
					File file = new File(localPath);
					if (!file.exists()) {
						file.mkdirs();
					}
				} else if (fileType.equals(FileType.EmptyFile)) {
					// 空文件
					File file = new File(localPath);
					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							logger.error("doRestore--> createNewFile IOException", file.getName());
						}
					}
				} else if (fileType.equals(FileType.DataFile)) {
					// 实体文件
					phyFileList.add(filePath);
				}
				for (String phyFileName : phyFileList) {
					File phyFile = new File(WfsEnv.ROOT_DIR + folder + File.separator + phyFileName);
					if (!phyFile.exists()) {
						logger.info("doRestore.putPhy:" + phyFileName);
						RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFileName);
						RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, WfsEnv.GROUP_FLAG);
						if (!WfsUtil.putPhy(wfsGet.getPhy(phyFileName), phyFile)) {
							logger.error("doRestore.putPhy-->false");
						}
					}
				}
			}
			logger.info("doRestore-->folder synchronized:" + folder);
		}
		duringRestore = false;
	}
}
