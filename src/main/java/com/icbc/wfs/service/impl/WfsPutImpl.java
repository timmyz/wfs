package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

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
	@Resource
	private WfsEditImpl wfsEditImpl;

	public boolean put(String path, String flag, InputStream in) {
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
			return false;
		} finally {
			try {
				if (null != out) {
					out.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				return false;
			}
		}

		FileInputStream nextIn = null;
		boolean ret = false;
		try {
			nextIn = new FileInputStream(phyFile);
			ret = put0(path, flag, nextIn);
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			try {
				if (null != nextIn) {
					nextIn.close();
				}
			} catch (IOException e) {
				return false;
			}
		}
		if (!ret) {
			phyFile.delete();
			return false;
		}
		return true;
	}

	public boolean put0(String path, String flag, InputStream in) {
		RpcContext.getContext().setAttachment(WfsRouter.ROUTE_KEY, path);
		flag = flag.concat(WfsEnv.GROUP_FLAG);
		RpcContext.getContext().setAttachment(WfsRouter.ROUTE_FLAG, flag);
		try {
			return wfsPut.put(path, flag, in);
		} catch (NoSuchElementException e) {
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
