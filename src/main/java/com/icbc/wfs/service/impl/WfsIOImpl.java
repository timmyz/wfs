package com.icbc.wfs.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsGet;
import com.icbc.wfs.service.WfsIO;
import com.icbc.wfs.service.WfsPut;

//@Service(version = "0.0.1")
@Service("wfsIOImpl")
public class WfsIOImpl implements WfsIO {

	@Resource
	private WfsPutImpl wfsPutImpl;

	@Resource
	private WfsPut wfsPut;
	@Resource
	private WfsGet wfsGet;

	/**
	 * 文件储存
	 */
	@Override
	public boolean put(String path, InputStream in) {

		/*
		 * 1. 请求应该为目录所在服务器，先读取写入目录文件 2.
		 * 
		 */

		RpcContext.getContext().setAttachment("routeKey", path);
		if (!wfsPut.put(path, in)) {
			return false;
		}

		if (!wfsPutImpl.put0(WfsUtil.getParent(path))) {
			wfsPut.del(path);
			return false;
		}
		return true;
	}

	@Override
	public InputStream get(String path) {
		RpcContext.getContext().setAttachment("routeKey", path);
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

		RpcContext.getContext().setAttachment("routeKey", path);

		String directory = WfsUtil.getParent(path);
		RpcContext.getContext().setAttachment("routeKey", directory);
		String fileName = WfsUtil.getFileName(path);
		if (!wfsPut.del(directory, fileName)) {
			return false;
		}

		RpcContext.getContext().setAttachment("routeKey", path);
		if (!wfsPut.del(path)) {
			return false;
		}

		return true;
	}

	/**
	 * 读取虚拟目录
	 */
	@Override
	public List<String> list(String path) {

		RpcContext.getContext().setAttachment("routeKey", path);
		List<String> list = new ArrayList<String>();

		try {

			InputStreamReader streamReader = new InputStreamReader(wfsGet.get(path));
			BufferedReader bufferedReader = new BufferedReader(streamReader);

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				list.add(line);
			}

			streamReader.close();
			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}
