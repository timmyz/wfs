package com.icbc.wfs;

import java.util.List;

import javax.annotation.Resource;

import com.icbc.dubbo.util.MurMurHash;
import com.icbc.wfs.service.impl.WfsGetImpl;

public class WfsRecover {

	@Resource
	private WfsGetImpl wfsGetImpl;
	
	
	public boolean doRecovery(){
		
		
		
//		List<String> fileList = wfsGetImpl.getList(path);
//		 String group[] = WfsEnv.GROUP.split("-");
//		
//		 int l = group.length-1;
//       
//         while (1 < l) {
//        	 getSubFoldere(group[--l], group[--l]);
//         }
		 
		 
		//WfsEnv.ROOT_DIR;
		return false;
	}
	
	private List<String> getSubFoldere (String begin, String end){
		return null;
//MurMurHash.nParseLong(begin);
//		for (char i = '\u0391'; i < '\u03A9'; i++) {
//	        out.append(i);
//	    }   
	}
}
