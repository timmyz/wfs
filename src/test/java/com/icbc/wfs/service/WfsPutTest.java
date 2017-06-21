package com.icbc.wfs.service;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WfsPutTest {

    @Resource(name = "wfsPut")
    private WfsPut wfsPut;

    public boolean put(String name, InputStream in) {
        return wfsPut.put(name, in);
    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext_test.xml");
        WfsPutTest wfsPutTest = context.getBean(WfsPutTest.class);
        wfsPutTest.put("/abcd/ef", new FileInputStream("B:\\Temp\\Chrysanthemum.jpg"));
        context.close();
    }
}
