package com.icbc.wfs.service;

import java.io.FileInputStream;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WfsIOTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		WfsIO wfsIO = context.getBean(WfsIO.class);
		if(wfsIO.put("/abcd/ef", new FileInputStream("/Users/timmyzhang/Downloads/Dash.zip"))){
			System.out.println("done");
		} else {
			System.out.println("oops~");
		}
		context.close();
	}
}
