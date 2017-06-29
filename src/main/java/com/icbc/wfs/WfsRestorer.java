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
import com.icbc.dubbo.router.HashRouter;
import com.icbc.dubbo.util.SpringContextHolder;
import com.icbc.wfs.service.WfsGet;

public class WfsRestorer implements Runnable {
    public static final long DELAY_TIME = 8000;

    private static Logger logger = LoggerFactory.getLogger(WfsRestorer.class);
    private static boolean DURING = false;

    public static boolean isDURING() {
        return DURING;
    }

    @Override
    public void run() {
        String rootDir = System.getProperty("duringRestore");
        if (rootDir != null) {
            return;
        }

        DURING = true;

        logger.info("prepare restore");
        try {
            Thread.sleep(DELAY_TIME);
        } catch (InterruptedException e) {
            logger.info("restore sleep error");
        }
        logger.info("do restore");

        WfsGet wfsGet = (WfsGet) SpringContextHolder.getApplicationContext().getBean("wfsGet",
                WfsGet.class);

        for (String folder : WfsEnv.HashFolders) {

            RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, folder);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, WfsEnv.GROUP_FLAG);
            List<String> fileList = null;

            logger.debug("fileList: " + fileList);
            try {
                fileList = wfsGet.getPhyList(folder, "");
            } catch (NoSuchElementException e) {
                logger.info("doRestore-->only this provider" + folder);
                continue;
            }
            if (null == fileList) {
                logger.info("doRestore-->null fileList " + folder);
                continue;
            }

            List<String> phyFileList = new LinkedList<String>();
            for (String path : fileList) {
                if (WfsUtil.isDirectory(path)) {
                    path = restoreDir(folder, path);
                } else {
                    restoreFile(folder, phyFileList, path);
                }

                for (String phyFileName : phyFileList) {
                    restorePhyFile(wfsGet, folder, phyFileName);
                }
            }
            logger.info("doRestore-->folder synchronized:" + folder);
        }
        logger.info("done restore");
        DURING = false;
    }

    private void restoreFile(String folder, List<String> phyFileList, String path) {
        File file = new File(WfsEnv.ROOT_DIR + folder + File.separator + path);
        if (WfsUtil.isHash(path)) {
            phyFileList.add(path);
        } else if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("doRestore--> createNewFile IOException", file.getName());
            }
        }
    }

    private String restoreDir(String folder, String path) {
        path = path.substring(0, path.length() - 1);
        File dir = new File(WfsEnv.ROOT_DIR + folder + File.separator + path);
        if (WfsUtil.isHash(path) && dir.exists()) {
            WfsUtil.delete(dir);
        }
        if (!dir.exists()) {
            logger.debug("directory not exist");
            if (!dir.mkdirs()) {
                logger.error("failed to mkdirs");
            }
        }
        return path;
    }

    private void restorePhyFile(WfsGet wfsGet, String folder, String phyFileName) {
        File phyFile = new File(WfsEnv.ROOT_DIR + folder + File.separator + phyFileName);
        if (phyFile.exists()) {
            logger.debug("phyFile exists");
            if (!phyFile.delete()) {
                logger.error("failed to delete");
            }
        }
        if (!phyFile.exists()) {
            logger.info("doRestore.putPhy:" + phyFileName);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFileName);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, WfsEnv.GROUP_FLAG);

            try {
                InputStream in = wfsGet.getPhy(phyFileName, "");
                if (null != in) {
                    logger.error("doRestore.getPhy--> " + phyFileName);
                }
            } catch (NoSuchElementException e) {
                logger.error("doRestore.getPhy-->only this provider" + folder);
            }
        }
    }
}
