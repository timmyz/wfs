package com.icbc.dubbo.router;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.container.spring.SpringContainer;
import com.icbc.dubbo.util.MurMurHash;
import com.icbc.dubbo.zk.ZKRegistryClient;

/**
 * 路由初始化
 * 
 * @author kfzx-wuzd
 * 
 */

public class RouterInitializer implements ApplicationListener<ContextStartedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouterInitializer.class);
    private ZKRegistryClient registryClient;

    @Override
    public void onApplicationEvent(ContextStartedEvent arg0) {
        Map<String, ServiceConfig<?>> serviceConfigs =
                (Map) SpringContainer.getContext().getBeansOfType(ServiceConfig.class);
        for (ServiceConfig<?> serviceConfig : serviceConfigs.values()) {
            Class<?> clazz = serviceConfig.getInterfaceClass();
            if (null != (BusiRouterFlag) clazz.getAnnotation(BusiRouterFlag.class)) {
                doRouter(serviceConfig, "business");
            }
            if (null != (WfsRouterFlag) clazz.getAnnotation(WfsRouterFlag.class)) {
                doRouter(serviceConfig, "wfsrouter");
            }
        }
    }

    private void doRouter(ServiceConfig<?> serviceConfig, String router) {
        try {
            String version = serviceConfig.getVersion();
            String interfaze = serviceConfig.getInterface();
            String path = "/dubbo/" + interfaze + "/" + Constants.ROUTERS_CATEGORY;
            List<URL> routers = registryClient.getChildren(Constants.ROUTERS_CATEGORY, interfaze,
                    version, null);

            boolean contain = false;
            for (URL oriRouter : routers) {
                if (router.equals(oriRouter.getParameter(Constants.ROUTER_KEY))) {
                    deleteOtherRouter(routers, path, router);
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                addRouter(interfaze, path, router);
                deleteOtherRouter(routers, path, router);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addRouter(String interfaze, String path, String router) {
        URL childUrl = URL.valueOf("routers://0.0.0.0/" + interfaze + "?category=routers");
        childUrl = childUrl.addParameter(Constants.CATEGORY_KEY, Constants.ROUTERS_CATEGORY)
                .addParameter(Constants.INTERFACE_KEY, interfaze)
                .addParameter(Constants.ROUTER_KEY, router)
                .addParameter(Constants.DYNAMIC_KEY, "false")
                .addParameter(Constants.TIMESTAMP_KEY, System.currentTimeMillis());

        String childPath = URL.encode(childUrl.toString());
        String newPath = path + "/" + childPath;
        registryClient.create(newPath, true);
        LOGGER.info("Registry router info: " + newPath);
    }

    private void deleteOtherRouter(List<URL> routers, String path, String router) {
        if (!routers.isEmpty()) {
            Iterator<URL> it = routers.iterator();
            while (it.hasNext()) {
                URL url = it.next();
                if (!router.equals(url.getParameter(Constants.ROUTER_KEY))) {
                    registryClient.delete(path + "/" + URL.encode(url.toString()));
                    it.remove();
                }
            }
        }
    }

    public void setRegistryClient(ZKRegistryClient registryClient) {
        this.registryClient = registryClient;
    }

    public ZKRegistryClient getRegistryClient() {
        return this.registryClient;
    }
}
