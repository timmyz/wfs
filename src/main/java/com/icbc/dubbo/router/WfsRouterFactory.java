package com.icbc.dubbo.router;

import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.alibaba.dubbo.rpc.cluster.RouterFactory;

/**
 * 路由工厂类
 * 
 * @author kfzx-wuzd
 * 
 */

public class WfsRouterFactory implements RouterFactory {

    private static final ConcurrentHashMap<String, WfsRouter> CACHE =
            new ConcurrentHashMap<String, WfsRouter>();

    @Override
    public Router getRouter(URL url) {
        String serviceKey = url.getServiceKey();
        WfsRouter router = CACHE.get(serviceKey);
        if (router == null) {
            CACHE.putIfAbsent(serviceKey, new WfsRouter(url));
            router = CACHE.get(serviceKey);
        }
        return router;
    }

}
