package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsPut;

//@Service(version = "0.0.1", cluster = "forking")
@Service("wfsPutImpl")
public class WfsPutImpl implements WfsPut {

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

		return true;
	}

	public boolean del(String path) {
		File phyFile = WfsUtil.getPhyFile(path);
		if (phyFile.exists()) {
			return phyFile.delete();
		}
		return true;
	}

	public boolean put0(String path) {
		String directory = WfsUtil.getParent(path);
		RpcContext.getContext().setAttachment("routeKey", directory);
		String fileName = WfsUtil.getFileName(path);
		return wfsPut.put(directory, fileName);
	}

	public boolean put(String directory, String fileName) {
		File phyFile = WfsUtil.getPhyFile(directory);
		if (phyFile.exists()) {
			// TODO modify virtual directory add fileName when not exist
			return true;
		} else if (!WfsUtil.ROOT.equals(directory)) {
			return put0(WfsUtil.getParent(directory));
		} else {
			// TODO first file make file WfsUtil.ROOT hash with fileName
			return true;
		}
	}

	public boolean del(String directory, String fileName) {
		// TODO modify virtual directory del fileName when exist
		return true;
	}

}
