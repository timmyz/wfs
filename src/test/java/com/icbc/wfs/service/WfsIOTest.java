package com.icbc.wfs.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
					String path = "/ab/cd/ef";
					in = new FileInputStream("B:\\Temp\\Chrysanthemum.jpg");
					if (wfsIO.put(path, in)) {
						System.out.println("put done");
						InputStream getIn = wfsIO.get(path);
						FileOutputStream out = null;
						try {
							out = new FileOutputStream("B:\\Chrysanthemum.jpg");
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
