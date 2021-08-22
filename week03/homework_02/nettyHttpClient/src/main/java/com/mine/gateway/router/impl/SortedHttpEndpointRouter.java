package com.mine.gateway.router.impl;

import com.mine.gateway.router.HttpEndpointRouter;

import java.util.List;

public class SortedHttpEndpointRouter implements HttpEndpointRouter {
    private volatile static int index = -1;

    @Override
    public synchronized String route(List<String> endpoints) {
        // 每次请求调用两次？
        index++;
        if(index==endpoints.size()){
            index = 0;
        }
        return endpoints.get(index);
    }
}
