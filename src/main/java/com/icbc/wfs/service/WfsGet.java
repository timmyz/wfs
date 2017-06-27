package com.icbc.wfs.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.icbc.dubbo.router.HashRouterFlag;

@HashRouterFlag
public interface WfsGet {

    InputStream get(String path, String flag) throws FileNotFoundException;

    List<String> getList(String path, String flag) throws FileNotFoundException;

    InputStream getPhy(String path, String flag);

    List<String> getPhyList(String path, String flag);
}
