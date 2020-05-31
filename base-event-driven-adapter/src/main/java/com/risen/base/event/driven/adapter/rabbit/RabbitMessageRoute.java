package com.risen.base.event.driven.adapter.rabbit;

import com.risen.base.event.driven.core.MessageRoute;

/**
 * Rabbit MQ 消息路由
 *
 * @author mengxr
 */
public class RabbitMessageRoute implements MessageRoute {
    private String exchange;

    private String routeKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }
}
