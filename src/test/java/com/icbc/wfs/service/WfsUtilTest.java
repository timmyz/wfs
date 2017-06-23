package com.icbc.wfs.service;

import com.icbc.wfs.WfsUtil;

public class WfsUtilTest {
	
	public static void main(String[] args) {
		System.out.println(WfsUtil.getParent("/aaaa/bbbb/ccccc"));
		System.out.println(WfsUtil.getParent("/aaaa/bbbb/"));
		System.out.println(WfsUtil.getParent("/aaaa/bbbb"));
		System.out.println(WfsUtil.getParent("/aaaa/"));
		System.out.println(WfsUtil.getParent("/aaaa"));
		System.out.println(WfsUtil.getParent("/"));
		System.out.println("/dawdwa".substring(0,1));
	}

}
