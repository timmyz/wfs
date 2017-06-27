package com.icbc.wfs.service.impl;

import java.util.List;

public class WfsGetImplTest {

    public static void main(String[] args) {
        System.setProperty("rootDir", "B:\\");
        System.setProperty("group", "0-8-A");
        List<String> list = new WfsGetImpl().getPhyList("00", "");
        System.out.println(list);
    }
}
