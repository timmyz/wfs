package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.WfsRouter;
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsPut;

@Service("wfsPutImpl")
public class WfsPutImpl implements WfsPut {
    // private static Logger logger = LoggerFactory.getLogger(WfsPutImpl.class);

    @Resource
    private WfsPut wfsPut;


    public boolean put(String path, InputStream in) {
        File phyFile = WfsUtil.getPhyFile(path);
        if (!phyFile.getParentFile().exists()) {
            if (!phyFile.getParentFile().mkdirs()) {
                return false;
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(phyFile);
            int n = -1;
            byte[] b = new byte[0x2000];
            while (-1 != (n = in.read(b))) {
                out.write(b, 0, n);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileInputStream myIn = null;
        try {
            myIn = new FileInputStream(phyFile);
        } catch (FileNotFoundException e) {
            return false;
        }
        return put0(path, myIn);
    }

    public boolean put0(String path, InputStream in) {
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
        String flag = RpcContext.getContext().getAttachment(WfsRouter.ROUTE_FLAG);
        RpcContext.getContext().setAttachment(WfsRouter.ROUTE_FLAG, flag.concat(WfsEnv.GROUP_FLAG));
        return wfsPut.put(path, in);
    }
}
