/*********************************************
 * Copyright (c) 2014 ICBC. All rights reserved. Created on 2016-9-23 下午02:36:43
 * 
 * Contributors: kfzx-xiaojy - initial implementation
 *********************************************/
package com.icbc.dubbo.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.RegistryConfig;
import com.icbc.dubbo.common.URLTimestampComparator;

public class ZKRegistryClient implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKRegistryClient.class);

    private RegistryConfig registryConfig;
    private ZooKeeper zk;

    public ZKRegistryClient() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        if (registryConfig == null) {
            throw new IllegalArgumentException("Property registryConfig can not be empty.");
        }
        connect(registryConfig.getAddress());
    }

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

    public List<URL> getChildren(String parent, String group) {
        List<String> paths;
        try {
            paths = zk.getChildren(parent, false);
        } catch (KeeperException e) {
            return Collections.emptyList();
        } catch (InterruptedException e) {
            return Collections.emptyList();
        }

        List<URL> urls = new ArrayList<URL>();
        for (String path : paths) {
            urls.add(URL.valueOf(URL.decode(path)));
        }
        if (group != null && group.length() > 0) {
            Iterator<URL> it = urls.iterator();
            while (it.hasNext()) {
                if (!group.equals(it.next().getParameter(Constants.GROUP_KEY))) {
                    it.remove();
                }
            }
        }
        return urls;
    }

    public List<URL> getChildren(String category, String serviceInterface, String version,
            String group) {
        String parent = getParentPath(category, serviceInterface, version);
        List<URL> urls = getChildren(parent, group);
        return urls;
    }

    public List<URL> getChildren(String category, URL url) {
        return getChildren(category, url.getParameter(Constants.INTERFACE_KEY),
                url.getParameter(Constants.VERSION_KEY), url.getParameter(Constants.GROUP_KEY));
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
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        int i = path.lastIndexOf('/');
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

    public void addOverrideParameters(String serviceInterface, String version, String group,
            Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }

        String parentPath =
                getParentPath(Constants.CONFIGURATORS_CATEGORY, serviceInterface, version);
        List<URL> children = getChildren(parentPath, group);
        URL oldUrl = null;
        URL newUrl = null;
        if (!children.isEmpty()) {
            Collections.sort(children, new URLTimestampComparator(false));
            for (URL childUrl : children) {
                if (StringUtils.isEquals(group, childUrl.getParameter(Constants.GROUP_KEY))) {
                    oldUrl = childUrl;
                    break;
                }
            }
            newUrl = oldUrl;
        }
        if (newUrl == null) {
            newUrl = URL.valueOf("override://0.0.0.0/" + serviceInterface);
            if (group != null) {
                newUrl = newUrl.addParameter(Constants.GROUP_KEY, group);
            }
        }
        newUrl = newUrl.addParameter(Constants.TIMESTAMP_KEY, System.currentTimeMillis());
        for (Entry<String, String> entry : params.entrySet()) {
            newUrl = newUrl.addParameter(entry.getKey(), entry.getValue());
        }

        create(parentPath + "/" + URL.encode(newUrl.toString()), true);
        if (null != oldUrl) {
            delete(parentPath + "/" + URL.encode(oldUrl.toString()));
        }
    }

    private String getParentPath(String category, String serviceInterface, String version) {
    	LOGGER.debug(version);
        return "/dubbo/" + serviceInterface + "/" + category;
    }


    public void setRegistryConfig(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }
}

/*
 * 修改历史 $Log: ,v $
 */
