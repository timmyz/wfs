/*********************************************
 * Copyright (c) 2014 ICBC. All rights reserved. Created on 2016-9-23 下午02:36:43
 * 
 * Contributors: kfzx-xiaojy - initial implementation
 *********************************************/
package com.icbc.dubbo.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;

public class ZKRegistryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKRegistryClient.class);
    private ZooKeeper zk;

    public ZKRegistryClient() {}

    public void connect(String address) throws IOException {
        zk = new ZooKeeper(address, 600000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {}
        });
    }

    public void close() {
        if (zk != null) {
            try {
                zk.close();
                zk = null;
            } catch (InterruptedException e) {
                LOGGER.warn(e);
            }
        }
    }

    public List<URL> getChildren(String category, String serviceInterface, String version) {
        List<String> paths;
        try {
            paths = zk.getChildren(Constants.PATH_SEPARATOR + Constants.DEFAULT_DIRECTORY
                    + Constants.PATH_SEPARATOR + serviceInterface + Constants.PATH_SEPARATOR
                    + category, false);
        } catch (KeeperException e) {
            return Collections.emptyList();
        } catch (InterruptedException e) {
            return Collections.emptyList();
        }

        List<URL> urls = new ArrayList<URL>();
        for (String path : paths) {
            urls.add(URL.valueOf(URL.decode(path)));
        }
        return urls;
    }

    public void delete(String path) {
        try {
            zk.delete(path, -1);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        } catch (KeeperException e) {
            LOGGER.warn(e);
        }
    }

    public void create(String path, boolean persistent) {
        int i = path.lastIndexOf(Constants.PATH_SEPARATOR);
        if (i > 0) {
            create(path.substring(0, i), persistent);
        }
        try {
            zk.create(path, null, Ids.OPEN_ACL_UNSAFE,
                    persistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL);
        } catch (KeeperException e) {
            LOGGER.warn(e);
        } catch (InterruptedException e) {
            LOGGER.warn(e);
        }
    }
    
}
