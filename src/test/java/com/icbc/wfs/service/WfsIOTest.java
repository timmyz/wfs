package com.icbc.wfs.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WfsIOTest {

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        // String inputPath = "/Users/timmyzhang/Downloads/Dash.zip";
        String inputPath = "B:\\Temp\\Chrysanthemum.jpg";
        // String inputPath = "";

        // String outputPath = "/Users/timmyzhang/temp/output/Dash.zip";
        String outputPath = "B:\\Chrysanthemum.jpg";
        // String outputPath = "";

        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext_test.xml");
        WfsIO wfsIO = context.getBean(WfsIO.class);
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
        context.close();
    }
}
