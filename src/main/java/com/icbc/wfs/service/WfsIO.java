package com.icbc.wfs.service;

import java.io.InputStream;
import java.util.List;

public interface WfsIO {
	
	/**
	 * 存放文件
	 * @param path 虚拟路径
	 * @param in 文件流
	 * @return 成功失败
	 */
	boolean put(String path, InputStream in);

	/**
	 * 获取文件
	 * @param path 文件流
	 * @return 文件流
	 */
	InputStream get(String path);
	
	/**
	 * 重命名文件
	 * @param newPath
	 * @param oldPath
	 * @return 成功失败
	 */
	boolean ren(String newPath, String oldPath);
	
	/**
	 * 删除文件
	 * @param path 虚拟路径
	 * @return 成功失败
	 */
	boolean del(String path);
	
	/**
	 * 获取目录
	 * @param path 虚拟路径
	 * @return 文件列表
	 */
	List<String> list(String path);
}
