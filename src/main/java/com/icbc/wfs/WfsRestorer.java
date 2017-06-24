package com.icbc.wfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import com.icbc.wfs.service.impl.WfsPutImpl;

public class WfsRestorer {

	private static Logger logger = LoggerFactory.getLogger(WfsRestorer.class);

	public static void doRestore() {

		logger.info("prepare restore");
		try {
			Thread.sleep(10000);
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
				if (fileList == null || fileList.isEmpty() || fileList.size() == 0) {
					logger.info("doRestore-->folder not exit:" + folder);
					continue;
				}
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
				String remotePath = fileInfoArr[1];

				// 本地物理路径=根目录+哈希
				String localPath = WfsEnv.ROOT_DIR + WfsUtil.PATH_SEPARATOR + remotePath;

				try {
					// 空文件
					if (fileType.equals(FileType.EmptyFile)) {
						File file = new File(localPath);
						if (!file.exists()) {
							file.createNewFile();
						}
					}
					// 实体文件
					else if (fileType.equals(FileType.DataFile)) {
						phyFileList.add(remotePath);

					}
					// 目录
					else if (fileType.equals(FileType.Directory)) {
						File file = new File(localPath);
						if (!file.exists()) {
							file.mkdirs();
						}
					}
				} catch (IOException e) {
					logger.error("doRestore-->false");
				}

				for (String phyFilePath : phyFileList) {

					File phyFile = new File(WfsEnv.ROOT_DIR + phyFilePath);

					if (!phyFile.exists()) {

						String phyFileName = phyFilePath.substring((WfsUtil.PATH_SEPARATOR + folder).length());
						
						logger.info("doRestore.putPhy:"+phyFileName);
						
						RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFileName);
						RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, WfsEnv.GROUP_FLAG);
						
						InputStream stream = wfsGet.getPhy(phyFileName);
						
						if (!WfsPutImpl.putPhy(stream, phyFile)) {
							logger.error("doRestore.putPhy-->false");
						}
					}
				}
			}
			logger.info("doRestore-->folder synchronized:" + folder);
		}
	}
}
