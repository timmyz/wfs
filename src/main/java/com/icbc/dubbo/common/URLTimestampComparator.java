/*********************************************
 * Copyright (c) 2014 ICBC.
 * All rights reserved.
 * Created on 2016-10-2 上午11:08:38

 * Contributors:
 *     kfzx-xiaojy - initial implementation
 *********************************************/
package com.icbc.dubbo.common;

import java.io.Serializable;
import java.util.Comparator;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;

public class URLTimestampComparator implements Comparator<URL>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7208016428461264960L;
	private boolean asc;
	
	public URLTimestampComparator(boolean asc){
		this.asc = asc;
	}

	@Override
	public int compare(URL url1, URL url2) {
		if(url1.getServiceKey().equals(url2.getServiceKey())){
        	// 按时间戳排序，最新的url放到最后面，成为最终使用生效的url
        	int timestampCompare = url1.getParameter(Constants.TIMESTAMP_KEY, "0")
        		.compareTo(url2.getParameter(Constants.TIMESTAMP_KEY, "0"));
        	if(timestampCompare != 0){
        		return asc ? timestampCompare : 0 - timestampCompare;
        	}
        }
    	return url1.getHost().compareTo(url2.getHost());
	}

}

/*
 * 修改历史
 * $Log: ,v $
*/