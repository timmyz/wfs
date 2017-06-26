package com.icbc.dubbo.router;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.container.spring.SpringContainer;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.icbc.dubbo.util.MurMurHash;

/**
 * 路由
 * 
 * @author kfzx-wuzd
 * 
 */
public class WfsRouter implements Router {
    public static final String NAME = "wfsrouter";
    
    public static final String ROUTE_KEY = "routeKey";
    public static final String ROUTE_FLAG = "routeFlag";

    private static final Logger LOGGER = LoggerFactory.getLogger(WfsRouter.class);

    private URL routerUrl;

    public WfsRouter(URL routerUrl) {
        this.routerUrl = routerUrl;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation)
            throws RpcException {
        String routeField = RpcContext.getContext().getAttachment(ROUTE_KEY);
        String routeFlag = RpcContext.getContext().getAttachment(ROUTE_FLAG);
        if (null == routeField) {
            return invokers;
        }
        if (null == routeFlag) {
            routeFlag = "";
        }
        List<Invoker<T>> result = findInvokers(invokers, MurMurHash.hash(routeField), routeFlag);
        return result;
    }

    protected <T> List<Invoker<T>> findInvokers(List<Invoker<T>> invokers, long hashValue,
            String routeFlag) {
        List<Invoker<T>> result = new ArrayList<Invoker<T>>();
        char minFlag = '|';
        Invoker<T> finalInvoker = null;
        for (Invoker<T> invoker : invokers) {
            try {
                String group[] = invoker.getUrl().getParameter("group").split("-");
                int l = group.length;
                char flag = group[--l].charAt(0);
                if (minFlag < flag || 0 <= routeFlag.indexOf(flag)) {
                    continue;
                }
                while (1 < l) {
                    if (MurMurHash.withInRange(hashValue, group[--l], group[--l])) {
                        finalInvoker = invoker;
                        minFlag = flag;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        if (null != finalInvoker) {
            result.add(finalInvoker);
        }
        return result;
    }

    public static String[] getServiceGroup(String name) {
        return SpringContainer.getContext().getBean("qpayentService", ServiceConfig.class)
                .getGroup().split("-");
    }

    @Override
    public int compareTo(Router o) {
        if (routerUrl == null || o.getUrl() == null) {
            return -1;
        }
        return routerUrl.toFullString().compareTo(o.getUrl().toFullString());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Router) {
            return compareTo((Router) o) == 0;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
    	int h = super.hashCode();
    	return h;
    }

    @Override
    public URL getUrl() {
        return routerUrl;
    }
}
