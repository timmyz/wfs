package com.icbc.wfs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.constant.FileType;
import com.icbc.dubbo.router.HashRouter;
import com.icbc.wfs.service.WfsGet;
import com.icbc.wfs.service.impl.WfsPutImpl;

public class WfsRestorer {

	private static Logger logger = LoggerFactory.getLogger(WfsRestorer.class);

	@Resource
	private WfsGet wfsGet;

	public void doRestore() {

		for (String folder : WfsEnv.HashFolders) {

			RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, folder);
			List<String> fileList = wfsGet.getPhyList(folder);

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

					File phyFile = WfsUtil.getPhyFile(WfsEnv.ROOT_DIR + phyFilePath);

					if (!phyFile.exists()) {
						RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFilePath);
						if (!WfsPutImpl.putPhy(wfsGet.getPhy(phyFilePath), phyFile)) {
							logger.error("doRestore.putPhy-->false");
						}
					}
				}
			}
		}
	}
}
