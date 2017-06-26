package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.WfsRouter;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsEdit;

@Service("wfsEditImpl")
public class WfsEditImpl implements WfsEdit {
    private static Logger logger = LoggerFactory.getLogger(WfsEditImpl.class);

    @Resource
    private WfsEdit wfsEdit;

    @Override
    public boolean[] del(String path) {
        File phyFile = WfsUtil.getPhyFile(path);
        if (phyFile.exists()) {
            if (!phyFile.delete()) {
                logger.error("del--> delete" + phyFile.getName());
                return new boolean[] {false};
            }
        }
        return new boolean[] {true};
    }

    public boolean[] put0(String path) {
        String directory = WfsUtil.getParent(path);
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, directory);
        String fileName = WfsUtil.getFileName(path);
        return wfsEdit.put(directory, fileName);
    }

    /*
     * 创建虚拟目录
     */
    @Override
    public boolean[] put(String dir, String fileName) {
        // 创建虚拟文件夹
        File vFolder = WfsUtil.getPhyFile(dir);
        if (!vFolder.exists()) {
            // 如果虚拟路径不等于根路径，则递归创建上级目录
            if (!WfsUtil.ROOT.equals(dir)) {
                if (!WfsUtil.mergerFalse(put0(dir))) {
                    logger.error("put--> put0");
                    return new boolean[] {false};
                }
            }
            if(!vFolder.mkdirs()){
            	logger.error("failed to mkdirs");
            }
        }
        // 创建假文件，HASH路径+真文件名
        File vFile = new File(WfsUtil.getPhyFilePath(dir) + WfsUtil.PATH_SEPARATOR + fileName);
        // 如果文件不存在，则创建
        if (!vFile.exists()) {
            if (WfsUtil.isDirectory(fileName)) {
                if (!vFile.mkdir()) {
                    logger.warn("put--> mkdir", vFile.getName());
                }
            } else {
                try {
                    if (!vFile.createNewFile()) {
                        logger.warn("put--> createNewFile", vFile.getName());
                    }
                } catch (IOException e) {
                    logger.error("put--> createNewFile IOException", vFile.getName());
                    return new boolean[] {false};
                }
            }
        }
        return new boolean[] {true};
    }

    @Override
    public boolean[] delDir(String directory, String fileName) {
        File dirPhyFile = WfsUtil.getPhyFile(directory);
        if (dirPhyFile.exists() && dirPhyFile.isDirectory()) {
            File phyFile = new File(dirPhyFile.getAbsolutePath() + File.separator + fileName);
            if (phyFile.exists()) {
                if (!phyFile.delete()) {
                    logger.error("del-->delete" + phyFile.getName());
                    return new boolean[] {false};
                }
            }
        }
        return new boolean[] {true};
    }

}
