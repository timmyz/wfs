package com.icbc.wfs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.HashRouter;
import com.icbc.dubbo.router.WfsRouter;
import com.icbc.wfs.service.WfsGet;
import com.icbc.wfs.service.impl.WfsGetImpl;

public class WfsRestorer {

	private static WfsRestorer instance;

	@Resource
	private WfsGet wfsGet;

	private WfsRestorer() {
	}

	public static synchronized WfsRestorer getInstance() {
		if (instance == null) {
			instance = new WfsRestorer();
		}
		return instance;
	}

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (String phyFilePath : phyFileList) {
					RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFilePath);
					// 本地物理路径=根目录+哈希
					wfsGet.getPhy(phyFilePath);
				}
			}
		}
	}
}
