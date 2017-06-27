package com.icbc.dubbo.router;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Router;

/**
 * 路由
 * 
 * @author kfzx-wuzd
 * 
 */
public abstract class AbsRouter implements Router {
    
    protected URL routerUrl;

    public AbsRouter(URL routerUrl) {
        this.routerUrl = routerUrl;
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
