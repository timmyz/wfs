package com.icbc;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringStartup {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		context.start();
		Thread.sleep(Long.MAX_VALUE);
		context.close();
	}
}
