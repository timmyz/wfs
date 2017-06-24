package com.icbc.wfs.service.impl;

import java.io.InputStream;
import java.util.List;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.WfsRouter;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsEdit;
import com.icbc.wfs.service.WfsGet;
import com.icbc.wfs.service.WfsIO;
import com.icbc.wfs.service.WfsPut;

// @Service(version = "0.0.1")
@Service("wfsIOImpl")
public class WfsIOImpl implements WfsIO {
    private static Logger logger = LoggerFactory.getLogger(WfsIOImpl.class);

    @Resource
    private WfsEditImpl wfsEditImpl;

    @Resource
    private WfsPut wfsPut;
    @Resource
    private WfsGet wfsGet;
    @Resource
    private WfsEdit wfsEdit;

    /**
     * 文件储存
     */
    @Override
    public boolean put(String path, InputStream in) {

        if (!WfsUtil.isDirectory(path)) {
            RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
            if (!wfsPut.put(path, "", in)) {
                logger.error("put-->false");
                return false;
            }
        }

        if (!WfsUtil.mergerFalse(wfsEditImpl.put0(path))) {
            wfsEdit.del(path);
        }
        return true;
    }

    @Override
    public InputStream get(String path) {
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        return wfsGet.get(path);
    }

    @Override
    public boolean ren(String newPath, String oldPath) {
        if (!put(newPath, get(oldPath))) {
            return false;
        }
        if (!del(oldPath)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean del(String path) {
        if (WfsUtil.isDirectory(path)) {
            // can not delete directory
            return false;
        }
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);

        String directory = WfsUtil.getParent(path);
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, directory);
        String fileName = WfsUtil.getFileName(path);
        if (!WfsUtil.mergerFalse(wfsEdit.delDir(directory, fileName))) {
            return false;
        }
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        return WfsUtil.mergerFalse(wfsEdit.del(path));
    }

    /**
     * IO层读取虚拟目录
     */
    @Override
    public List<String> list(String path) {
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        List<String> list = wfsGet.getList(path);
        return list;
    }
}
