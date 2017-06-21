package com.icbc.wfs.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.icbc.wfs.service.WfsPut;

//@Service(version = "0.0.1", cluster = "forking", path = "wfs/wfsIO")
 @Service("wfsIOImpl")
public class WfsPutImpl implements WfsPut {

	@Reference(version = "0.0.1")
	private WfsPut wfsPut;

	public boolean put(String name, InputStream in) {

		FileOutputStream out = null;
		try {
			out = new FileOutputStream("B:\\" + System.nanoTime());
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

		System.out.println(name);
		return true;
	}

}
