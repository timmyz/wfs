package com.icbc;

import com.alibaba.dubbo.container.spring.SpringContainer;

public class SpringStartup {

    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub
        if (null == System.getProperty(SpringContainer.SPRING_CONFIG)) {
            System.setProperty(SpringContainer.SPRING_CONFIG, "applicationContext.xml");
        }
        SpringContainer container = new SpringContainer();
        container.start();
        Thread.sleep(Long.MAX_VALUE);
        container.stop();
    }
}
