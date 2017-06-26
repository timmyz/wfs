package com.icbc.wfs.service.impl;

import java.io.BufferedInputStream;
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
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsGet;

// @Service(version = "0.0.1")
@Service("wfsGetImpl")
public class WfsGetImpl implements WfsGet {
    private static Logger logger = LoggerFactory.getLogger(WfsGetImpl.class);

    @Override
    public InputStream get(String path) {
        return getPhy0(WfsUtil.getPhyFile(path));
    }

    @Override
    public InputStream getPhy(String path) {
        return getPhy0(new File(WfsUtil.getPhyFilePathByHash(path)));
    }

    private InputStream getPhy0(File file) {
        BufferedInputStream fin = null;
        try {
            fin = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.warn("getPhy0-->FileNotFoundException" + file.getName(), e);
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION,
                    "FileNotFoundException" + file.getName(), e);
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
            logger.warn("getList-->not exists or is directory" + path);
            throw new RpcException(RpcException.FORBIDDEN_EXCEPTION);
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
        List<String> fileList = new LinkedList<String>();
        File dir = new File(WfsEnv.ROOT_DIR + path);
        if (dir.exists() && dir.isDirectory()) {
            File[] fileArr = dir.listFiles();
            if (fileArr == null) {
                return fileList;
            }
            for (File file : fileArr) {
                if (!WfsUtil.isHash(file.getName())) {
                    logger.error("getPhyList:" + file.getPath() + " is not valid");
                    continue;
                }

                fileList.add(WfsUtil.getName(file));
                if (file.isDirectory()) {
                    File[] subFileArr = file.listFiles();
                    if (subFileArr == null) {
                        continue;
                    }
                    for (File subFile : subFileArr) {
                        fileList.add(
                                file.getName() + WfsUtil.PATH_SEPARATOR + WfsUtil.getName(subFile));
                    }
                }
            }
        }
        return fileList;
    }
}
