package com.icbc.wfs.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public class WfsIOTest {

    // TODO Auto-generated method stub
    // public static final String inputPath = "/Users/timmyzhang/Downloads/Dash.zip";
    public static final String inputPath = "B:\\Temp\\NOTES_9.0.1_MAC_64B_ZHCN_ZHTW.dmg";
    // public static final String inputPath = "";

    // public static final String outputPath = "/Users/timmyzhang/temp/output/Dash.zip";
    public static final String outputPath = "B:\\NOTES_9.0.1_MAC_64B_ZHCN_ZHTW.dmg";
    // public static final String outputPath = "";

    public static void main(String[] args) throws Exception {

        ApplicationConfig application = new ApplicationConfig();
        application.setName("wfs-test");
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
        registry.setAddress("122.26.13.124:2181,122.26.13.125:2181,122.26.13.126:2181");
        registry.setCheck(false);

        ReferenceConfig<WfsIO> reference = new ReferenceConfig<WfsIO>();
        reference.setApplication(application);
        reference.setTimeout(300000);
        reference.setRegistry(registry);
        reference.setCheck(false);
        reference.setInterface(WfsIO.class);
        reference.setVersion("0.0.1");

        WfsIO wfsIO = reference.get();

        boolean fst = true;
        while (true) {
            if (fst || '\n' == System.in.read()) {
                fst = false;
                FileInputStream in = null;
                try {
                    String path = "/ab/cd/ef";
                    in = new FileInputStream(inputPath);
                    if (wfsIO.put(path, in)) {
                        System.out.println("put done");
                        InputStream getIn = wfsIO.get(path);
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(outputPath);
                            int n = -1;
                            byte[] b = new byte[0x2000];
                            while (-1 != (n = getIn.read(b))) {
                                out.write(b, 0, n);
                            }
                            out.flush();
                        } finally {
                            if (null != out) {
                                out.close();
                            }
                            if (null != getIn) {
                                getIn.close();
                            }
                        }
                        if (wfsIO.del(path)) {
                            System.out.println("del  done");
                        } else {
                            System.out.println("del  oops");
                        }
                    } else {
                        System.out.println("put oops~");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable t) {
                    break;
                } finally {
                    try {
                        if (null != in) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
