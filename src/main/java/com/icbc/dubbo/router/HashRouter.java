package com.icbc.dubbo.router;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.icbc.dubbo.util.MurMurHash;

/**
 * 路由
 * 
 * @author kfzx-wuzd
 * 
 */
public class HashRouter extends AbsRouter {
    public static final String NAME = "hashrouter";

    public static final String ROUTE_KEY = "routeKey";
    public static final String ROUTE_VALUE = "routeValue";
    public static final String ROUTE_FLAG = "routeFlag";

    private static final Logger LOGGER = LoggerFactory.getLogger(HashRouter.class);

    public HashRouter(URL routerUrl) {
        super(routerUrl);
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation)
            throws RpcException {
        long hash;
        String routeValue = RpcContext.getContext().getAttachment(ROUTE_VALUE);
        if (null != routeValue) {
            hash = MurMurHash.nParseLong(routeValue);
        } else {
            String routeField = RpcContext.getContext().getAttachment(ROUTE_KEY);
            if (null == routeField) {
                return invokers;
            } else {
                hash = MurMurHash.hash(routeField);
            }
        }
        String routeFlag = RpcContext.getContext().getAttachment(ROUTE_FLAG);
        List<Invoker<T>> result = findInvokers(invokers, hash, routeFlag);
        return result;
    }

    protected <T> List<Invoker<T>> findInvokers(List<Invoker<T>> invokers, long hashValue,
            String routeFlag) {
        List<Invoker<T>> result = new ArrayList<Invoker<T>>();
        for (Invoker<T> invoker : invokers) {
            try {
                if (MurMurHash.withInRange(invoker.getUrl().getParameter("group").split("-"),
                        hashValue, routeFlag, false)) {
                    result.add(invoker);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return result;
    }

}
