package com.icbc.wfs.service;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WfsIOTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		WfsIO wfsIO = context.getBean(WfsIO.class);
		boolean fst = true;
		while (true) {
			if (fst || '\n' == System.in.read()) {
				fst = false;
				FileInputStream in = null;
				try {
					in = new FileInputStream("B:\\Temp\\Chrysanthemum.jpg");
					if (wfsIO.put("/ab/cd/ef", in)) {
						System.out.println("done");
					} else {
						System.out.println("oops~");
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
