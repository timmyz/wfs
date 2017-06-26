package com.icbc.wfs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.icbc.dubbo.router.HashRouter;
import com.icbc.dubbo.util.SpringContextHolder;
import com.icbc.wfs.service.WfsGet;

public class WfsRestorer implements Runnable {
    public static final long DELAY_TIME = 8000;

    private static Logger logger = LoggerFactory.getLogger(WfsRestorer.class);
    private static boolean duringRestore = false;

    public static boolean isDuringRestore() {
        return duringRestore;
    }

    public static void setDuringRestore(boolean duringRestore) {
        WfsRestorer.duringRestore = duringRestore;
    }

    @Override
    public void run() {
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

        WfsGet wfsGet = (WfsGet) SpringContextHolder.getApplicationContext().getBean("wfsGet",
                WfsGet.class);

        for (String folder : WfsEnv.HashFolders) {

            RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, folder);
            RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG, WfsEnv.GROUP_FLAG);
            List<String> fileList = new LinkedList<String>();

            try {
                fileList = wfsGet.getPhyList(folder);
                System.out.println(fileList);
            } catch (NoSuchElementException e) {
                continue;
            } catch (RpcException e) {
                logger.warn("provider is during too!", e);
                continue;
            }

            List<String> phyFileList = new LinkedList<String>();
            for (String path : fileList) {
                if (WfsUtil.isDirectory(path)) {
                    path = path.substring(0, path.length() - 1);
                    File dir = new File(WfsEnv.ROOT_DIR + folder + File.separator + path);
                    if (WfsUtil.isHash(path) && dir.exists()) {
                        WfsUtil.delete(dir);
                    }
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
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

                for (String phyFileName : phyFileList) {
                    File phyFile =
                            new File(WfsEnv.ROOT_DIR + folder + File.separator + phyFileName);
                    if (phyFile.exists()) {
                        phyFile.delete();
                    }
                    if (!phyFile.exists()) {
                        logger.info("doRestore.putPhy:" + phyFileName);
                        RpcContext.getContext().setAttachment(HashRouter.ROUTE_VALUE, phyFileName);
                        RpcContext.getContext().setAttachment(HashRouter.ROUTE_FLAG,
                                WfsEnv.GROUP_FLAG);
                        if (!WfsUtil.putPhy(wfsGet.getPhy(phyFileName), phyFile)) {
                            logger.error("doRestore.putPhy-->false");
                        }
                    }
                }
            }
            logger.info("doRestore-->folder synchronized:" + folder);
        }
        duringRestore = false;
        logger.info("done restore");
    }
}
