package com.icbc.wfs.service;

import java.util.List;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class WfsGetTest {

    public static void main(String[] args) throws Exception {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("wfs-test");
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
        registry.setAddress("122.26.13.124:2181,122.26.13.125:2181,122.26.13.126:2181");
        registry.setCheck(false);

        ReferenceConfig<WfsGet> reference = new ReferenceConfig<WfsGet>();
        reference.setApplication(application);
        reference.setTimeout(300000);
        reference.setRegistry(registry);
        reference.setCheck(false);
        reference.setInterface(WfsGet.class);
        reference.setGroup("*");

        WfsGet wfsGet = reference.get();

        List<String> list = wfsGet.getPhyList("62", "");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
