package com.icbc.wfs.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.icbc.dubbo.util.MurMurHash;
import com.icbc.wfs.WfsEnv;
import com.icbc.wfs.service.WfsGet;

//@Service(version = "0.0.1")
@Service("wfsGetImpl")
public class WfsGetImpl implements WfsGet {

	@Override
	public InputStream get(String path) {
		// TODO Auto-generated method stub
		String hash = MurMurHash.hashRange(path);
		File phyFile = new File(WfsEnv.rootDir + hash.substring(0, 2) + File.separator + hash);

		try {
			return new FileInputStream(phyFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}

	}

}
