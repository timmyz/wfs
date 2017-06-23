package com.icbc.wfs.service.impl;

import java.io.InputStream;
import java.util.List;
import javax.annotation.Resource;
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

        /*
         * 1. 请求应该为目录所在服务器，先读取写入目录文件 2.
         * 
         */

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        if (!wfsPut.put(path, "", in)) {
            return false;
        }

        if (!wfsEditImpl.put0(WfsUtil.getParent(path))) {
            wfsEdit.del(path);
            return false;
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
        if (path.endsWith(WfsUtil.PATH_SEPARATOR)) {
            // can not delete directory
            return false;
        }
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);

        String directory = WfsUtil.getParent(path);
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, directory);
        String fileName = WfsUtil.getFileName(path);
        if (!wfsEdit.del(directory, fileName)) {
            return false;
        }

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        if (!wfsEdit.del(path)) {
            return false;
        }

        return true;
    }

    /**
     * IO层读取虚拟目录
     */
    @Override
    public List<String> list(String path) {

        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        List<String> list = wfsGet.getList(path);
        return list;

        // 更换实现方法，这个作废
        // try {
        //
        // InputStreamReader streamReader = new
        // InputStreamReader(wfsGet.get(path));
        // BufferedReader bufferedReader = new BufferedReader(streamReader);
        //
        // String line = null;
        //
        // while ((line = bufferedReader.readLine()) != null) {
        // list.add(line);
        // }
        //
        // streamReader.close();
        // bufferedReader.close();
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }
}
