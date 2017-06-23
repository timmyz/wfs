package com.icbc.wfs.service;

import java.io.InputStream;

import com.icbc.dubbo.router.WfsRouterFlag;

@WfsRouterFlag
public interface WfsPut {
    boolean put(String path, InputStream in);
}
