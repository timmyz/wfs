package com.icbc.wfs.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.dubbo.router.WfsRouter;
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsPut;

@Service("wfsPutImpl")
public class WfsPutImpl implements WfsPut {
	private static Logger logger = LoggerFactory.getLogger(WfsPutImpl.class);

	@Resource
	private WfsPut wfsPut;

	public boolean put(String path, String flag, InputStream in) {

		File phyFile = WfsUtil.getPhyFile(path);

		if (!WfsUtil.putPhy(in, phyFile)) {
			return false;
		}

		boolean ret = false;
		FileInputStream fin = null;
		BufferedInputStream nextIn = null;
		try {
			fin = new FileInputStream(phyFile);
			nextIn = new BufferedInputStream(fin);
			RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
			flag = flag.concat(WfsEnv.GROUP_FLAG);
			RpcContext.getContext().setAttachment(WfsRouter.ROUTE_FLAG, flag);
			ret = wfsPut.put(path, flag, nextIn);
		} catch (NoSuchElementException e) {
			ret = true;
		} catch (IOException e) {
			logger.error("put-->IOException", e);
			ret = false;
		} finally {
			try {
				if (null != nextIn) {
					nextIn.close();
				}
				if (null != fin) {
					fin.close();
				}
			} catch (IOException e) {
				logger.error("put--> close IOException", e);
				ret = false;
			}
		}
		if (!ret) {
			if(phyFile.delete()){
            	logger.error("failed to delete");	
			}
			return false;
		}
		return true;
	}
}
