package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcException;
import com.icbc.dubbo.constant.FileType;
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsGet;

// @Service(version = "0.0.1")
@Service("wfsGetImpl")
public class WfsGetImpl implements WfsGet {
    private static Logger logger = LoggerFactory.getLogger(WfsGetImpl.class);

    @Override
    public InputStream get(String path) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(WfsUtil.getPhyFile(path));
        } catch (FileNotFoundException e) {
            logger.warn("get-->FileNotFoundException" + path, e);
            throw new RpcException();
        }
        return fin;
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
	@Override
	public List<String> getPhyList(String path) {

		File file = new File(WfsEnv.ROOT_DIR + WfsUtil.PATH_SEPARATOR + path);

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

						String str = FileType.EmptyFile;

						if (fileOne.length() > 0) {
							try {
								MurMurHash.nParseLong(fileOne.getName());
							} catch (NumberFormatException e) {
								logger.error("getPhyList:" + fileOne.getPath() + " is not valid file");
								continue;
							}
							str = FileType.DataFile;
						}

						fileList.add(str + ":" + fileOne.getPath().substring(WfsEnv.ROOT_DIR.length()));

					} else if (fileOne.isDirectory()) {

						fileList.add(FileType.Directory + ":" + fileOne.getPath().substring(WfsEnv.ROOT_DIR.length()));
						String childPath = path + WfsUtil.PATH_SEPARATOR + fileOne.getPath().substring(file.getPath().length()+1);
						List<String> tmpList = getPhyList(childPath);
						if (tmpList != null) {
							fileList.addAll(tmpList);
						}

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
