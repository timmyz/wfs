package com.icbc.wfs.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.HashRouter;
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.WfsRestorer;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsGet;

// @Service(version = "0.0.1")
@Service("wfsGetImpl")
public class WfsGetImpl implements WfsGet {
    private static Logger logger = LoggerFactory.getLogger(WfsGetImpl.class);

    @Resource
    private WfsGet wfsGet;

    private InputStream getPhy0(File file) throws FileNotFoundException {
        BufferedInputStream fin = null;
        try {
            fin = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.warn("getPhy0-->FileNotFoundException" + file.getName(), e);
            throw e;
        }
        return fin;
    }

    @Override
    public InputStream get(String path, String flag) throws FileNotFoundException {
        try {
            if (WfsRestorer.isDURING()) {
                throw new FileNotFoundException(path);
            }
            return getPhy0(WfsUtil.getPhyFile(path));
        } catch (FileNotFoundException e) {
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_KEY, path);
            flag = flag.concat(WfsEnv.GROUP_FLAG);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, flag);
            try {
                return wfsGet.get(path, flag);
            } catch (NoSuchElementException e1) {
                throw e;
            }
        }
    }

    /**
     * 获取目录文件列表
     * 
     * @throws FileNotFoundException
     */
    @Override
    public List<String> getList(String path, String flag) throws FileNotFoundException {
        File dir = WfsUtil.getPhyFile(path);
        if (!WfsRestorer.isDURING() && dir.exists() && dir.isDirectory()) {
            List<String> fileList = new LinkedList<String>();
            File[] fileArray = dir.listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    fileList.add(WfsUtil.getName(file));
                }
            }
            return fileList;
        } else {
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_KEY, path);
            flag = flag.concat(WfsEnv.GROUP_FLAG);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, flag);
            try {
                return wfsGet.getList(path, flag);
            } catch (NoSuchElementException e1) {
                throw new FileNotFoundException();
            }
        }
    }

    @Override
    public InputStream getPhy(String path, String flag) {
        try {
            if (WfsRestorer.isDURING()) {
                throw new FileNotFoundException(path);
            }
            return getPhy0(new File(WfsUtil.getPhyFilePathByHash(path)));
        } catch (FileNotFoundException e) {
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_KEY, path);
            flag = flag.concat(WfsEnv.GROUP_FLAG);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, flag);
            try {
                return wfsGet.getPhy(path, flag);
            } catch (NoSuchElementException e1) {
                return null;
            }
        }
    }

    /**
     * 获取目录下所有文件及文件夹
     * 
     * @throws FileNotFoundException
     */
    @Override
    public List<String> getPhyList(String path, String flag) {
        File dir = new File(WfsEnv.ROOT_DIR + path);
        if (!WfsRestorer.isDURING() && dir.exists() && dir.isDirectory()) {
            List<String> fileList = new LinkedList<String>();
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
            return fileList;
        } else {
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_KEY, path);
            flag = flag.concat(WfsEnv.GROUP_FLAG);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, flag);
            try {
                return wfsGet.getPhyList(path, flag);
            } catch (NoSuchElementException e1) {
                return null;
            }
        }
    }
}
