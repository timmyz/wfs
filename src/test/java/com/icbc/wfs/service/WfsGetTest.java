package com.icbc.wfs.service;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WfsGetTest {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext_test.xml");
        WfsGet wfsGet = context.getBean(WfsGet.class);
        List<String> list = wfsGet.getPhyList("62", "");
        for (String s : list) {
            System.out.println(s);
        }
        context.close();
    }
}
