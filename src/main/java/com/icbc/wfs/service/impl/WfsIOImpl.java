package com.icbc.wfs.service.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
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
     * 存放文件
     * 
     * @param path 虚拟路径
     * @param in 文件流
     * @return 成功失败
     */
    @Override
    public boolean put(String path, InputStream in) {

        if (WfsUtil.isEmptyString(path)) {
            throw new RuntimeException("虚拟路径为空");
        }

        if (in == null && !WfsUtil.isDirectory(path)) {
            throw new RuntimeException("文件流为空");
        }

        if (!WfsUtil.isDirectory(path)) {
            RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
            if (!wfsPut.put(path, "", in)) {
                logger.error("put-->false");
                throw new RuntimeException("存放失败[1]");
            }
        }
        if (!WfsUtil.mergerFalse(wfsEditImpl.put0(path))) {
            wfsEdit.del(path);
            throw new RuntimeException("存放失败[2]");
        }
        return true;
    }

    /**
     * 获取文件
     * 
     * @param path 虚拟路径
     * @return 文件流
     */
    @Override
    public InputStream get(String path) {

        if (WfsUtil.isEmptyString(path)) {
            throw new RuntimeException("虚拟路径为空");
        }

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        InputStream in;
        try {
            in = wfsGet.get(path, "");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("找不到文件");
        }
        return in;
    }

    /**
     * 重命名文件
     * 
     * @param newPath 新虚拟路径
     * @param oldPath 原虚拟路径
     * @return 成功失败
     */
    @Override
    public boolean ren(String newPath, String oldPath) {

        // 新虚拟路径为空
        if (WfsUtil.isEmptyString(newPath)) {
            throw new RuntimeException("新虚拟路径为空");
        }
        // 原虚拟路径为空
        if (WfsUtil.isEmptyString(oldPath)) {
            throw new RuntimeException("原虚拟路径为空");
        }
        // 新虚拟路径=原虚拟路径
        if (newPath.equals(oldPath)) {
            throw new RuntimeException("新虚拟路径不能等于原虚拟路径");
        }

        if (!put(newPath, get(oldPath))) {
            throw new RuntimeException("重命名文件失败[1]");
        }
        if (!del(oldPath)) {
            throw new RuntimeException("重命名文件失败[2]");
        }
        return true;
    }

    /**
     * 删除文件
     * 
     * @param path 虚拟路径
     * @return 成功失败
     */
    @Override
    public boolean del(String path) {

        // 虚拟路径为空
        if (WfsUtil.isEmptyString(path)) {
            throw new RuntimeException("虚拟路径为空");
        }

        // 虚拟路径为目录
        if (WfsUtil.isDirectory(path)) {
            throw new RuntimeException("虚拟路径为目录");
        }

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);

        String directory = WfsUtil.getParent(path);
        String fileName = WfsUtil.getFileName(path);

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, directory);

        if (!WfsUtil.mergerFalse(wfsEdit.delDir(directory, fileName))) {
            throw new RuntimeException("删除失败[1]");
        }

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);

        if (!WfsUtil.mergerFalse(wfsEdit.del(path))) {
            throw new RuntimeException("删除失败[2]");
        }

        return true;
    }

    /**
     * 获取目录
     * 
     * @param path 虚拟路径
     * @return 文件列表
     */
    @Override
    public List<String> list(String path) {

        // 虚拟路径为空
        if (WfsUtil.isEmptyString(path)) {
            throw new RuntimeException("虚拟路径为空");
        }

        List<String> list = new LinkedList<String>();
        if (!WfsUtil.isDirectory(path)) {
            list.add(WfsUtil.getFileName(path));
        } else {
            RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
            try {
                list = wfsGet.getList(path, "");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("找不到文件");
            }
        }
        return list;
    }
}
