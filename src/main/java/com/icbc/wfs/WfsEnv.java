package com.icbc.wfs;

public class WfsEnv {
    public static final String ROOT_DIR = System.getProperty("rootDir");
    public static final String GROUP = System.getProperty("dubbo.service.group");
    public static final String GROUP_FLAG = GROUP.substring(GROUP.lastIndexOf("-") + 1);
}
