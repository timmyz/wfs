package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.icbc.dubbo.util.MurMurHash;
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsGet;
import com.icbc.wfs.service.WfsPut;

//@Service(version = "0.0.1", cluster = "forking")
@Service("wfsPutImpl")
public class WfsPutImpl implements WfsPut {

	@Resource
	private WfsPut wfsPut;
	@Resource
	private WfsGet wfsGet;

	public boolean put(String path, InputStream in) {
		String hash = MurMurHash.hashRange(path);
		File phyFile = new File(WfsEnv.rootDir + hash.substring(0, 2) + File.separator + hash);
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

		if (!WfsUtil.ROOT.equals(WfsUtil.getParent(path))) {
			if (!wfsPut.put(path)) {
				return false;
			}
		}

		return true;
	}

	public boolean put(String path) {
		// TODO modify virtual directory
		return true;
	}

}
