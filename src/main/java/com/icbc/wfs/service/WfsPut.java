package com.icbc.wfs.service;

import java.io.InputStream;

import com.icbc.dubbo.router.HashRouterFlag;

@HashRouterFlag
public interface WfsPut {
    boolean put(String path, String flag, InputStream in);
}
