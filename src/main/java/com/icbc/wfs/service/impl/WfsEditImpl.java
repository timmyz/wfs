package com.icbc.wfs.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.rpc.RpcContext;
import com.icbc.wfs.WfsUtil;
import com.icbc.wfs.service.WfsEdit;

@Service("wfsEditImpl")
public class WfsEditImpl implements WfsEdit {
	private static Logger logger = LoggerFactory.getLogger(WfsEditImpl.class);

	@Resource
	private WfsEdit wfsEdit;

	@Override
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
		return wfsEdit.put(directory, fileName);
	}

	@Override
	public boolean put(String dir, String fileName) {

		try {

			File vFolder = WfsUtil.getPhyFile(dir);
			if (!vFolder.exists()) {
				vFolder.mkdirs();
			}

			// 创建假文件，HASH路径+真文件名
			File vFile = new File(WfsUtil.getPhyFilePath(dir) + fileName);

			if (!vFile.exists()) {
				vFile.createNewFile();
			}

			// 如果虚拟路径不等于根路径，则创建父路
			if (!WfsUtil.ROOT.equals(dir)) {

				String prntDir = WfsUtil.getParent(dir);
				String grndPrntDir = WfsUtil.getParent(prntDir);
				String prntFolder = WfsUtil.getFileName(prntDir);
				
				//按照父目录，调用
				RpcContext.getContext().setAttachment("routeKey", prntDir);
				return wfsEdit.put(grndPrntDir, prntFolder);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public boolean del(String directory, String fileName) {
		// TODO modify virtual directory del fileName when exist

		/*
		 * 大致逻辑： 先判断当前要删的文件的目录是不是否在当前物理机中存在，如果不存在就直接结束；
		 * 如果目录文件存在，则需要将这个目录下的对应文件那一行给找出来删掉，这个过程没法简单删除一行，
		 * 我的做法是写一个临时文件，把要删除的那一行过滤掉，最后把原文件删掉，临时文件变成正式文件替换掉；
		 * 最后目录都改成功后，再去调删文件的服务，广播出去删目标文件，至于成不成功就不关心了；
		 * 
		 * TODO: 临提交前又想了下，似乎我这里不应该再去做删文件的事情了，应该是删文件的时候，
		 * 发现如果自己的物理机下有文件，先来调我这个服务去改目录，然后再删本机文件，这样似乎比较顺。 先记着，明天讨论下，睡觉
		 * 
		 */
		File dirPhyFile = WfsUtil.getPhyFile(directory);
		if (dirPhyFile.exists()) {
			BufferedReader br = null;
			BufferedWriter wr = null;
			File tmpDirFile = new File(dirPhyFile.getPath() + ".tmp");
			if (tmpDirFile.exists()) {
				tmpDirFile.delete();
			}
			try {
				br = new BufferedReader(new FileReader(dirPhyFile));
				wr = new BufferedWriter(new FileWriter(tmpDirFile));

				String line = null;
				while ((line = br.readLine()) != null) {
					if (fileName.equals(line)) {
						continue;
					}
					wr.write(line);
				}

			} catch (FileNotFoundException e) {
				logger.error("del", e);
			} catch (IOException e) {
				logger.error("del", e);
			} finally {
				if (wr != null) {
					try {
						wr.close();
					} catch (IOException e) {
						logger.error("fail when close writer", e);
					}
				}
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						logger.error("fail when close reader", e);
					}
				}
			}
			tmpDirFile = new File(dirPhyFile.getPath() + ".tmp");
			while (dirPhyFile.exists()) {
				dirPhyFile.delete();
			}
			tmpDirFile.renameTo(dirPhyFile);

			// String fileFullPath = directory + WfsUtil.PATH_SEPARATOR +
			// fileName;
			return true;// wfsIO.del(fileFullPath); 这里只负责动目录，删文件是IOImpl里做的
			// 这里有点疑问，forking执行是每台都会做一遍的，有文件的会true，没文件的会false，那到底返回true还是false？
		} else {
			return false;
		}
	}

}
