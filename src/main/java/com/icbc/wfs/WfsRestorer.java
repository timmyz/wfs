package com.icbc.wfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.HashRouter;
import com.icbc.wfs.service.WfsGet;
import com.icbc.wfs.service.impl.WfsPutImpl;

public class WfsRestorer {

	private static Logger logger = LoggerFactory.getLogger(WfsRestorer.class);

	@Resource
	private WfsGet wfsGet;
	private WfsPutImpl wfsPutImpl;

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
					if (fileType.equals("0")) {
						File file = new File(localPath);
						if (!file.exists()) {
							file.createNewFile();
						}
					}
					// 实体文件
					else if (fileType.equals("1")) {
						phyFileList.add(remotePath);

					}
					// 目录
					else if (fileType.equals("2")) {
						File file = new File(localPath);
						if (!file.exists()) {
							file.mkdirs();
						}
					}
				} catch (IOException e) {
					logger.error("put-->false");
				}

				for (String phyFilePath : phyFileList) {
					RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFilePath);
					File phyFile = WfsUtil.getPhyFile(WfsEnv.ROOT_DIR + phyFilePath);

					if (!phyFile.exists()) {

						InputStream stream = wfsGet.getPhy(phyFilePath);
						FileOutputStream out = null;
						try {
							out = new FileOutputStream(phyFile);
							int n = -1;
							byte[] b = new byte[0x2000];
							while (-1 != (n = stream.read(b))) {
								out.write(b, 0, n);
							}
							out.flush();
						} catch (Exception e) {
							logger.error("put-->Exception");
						} finally {
							try {
								if (null != out) {
									out.close();
								}
								if (null != stream) {
									stream.close();
								}
							} catch (IOException e) {
								logger.error("put-->close Exception", e);
							}
						}
					}

				}
			}
		}
	}
}
