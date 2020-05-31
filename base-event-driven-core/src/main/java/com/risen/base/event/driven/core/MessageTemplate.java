package com.risen.base.event.driven.core;

/**
 * 消息模版
 *
 * @author mengxr
 */
public interface MessageTemplate {
    /**
     * 发布消息
     *
     * @param event
     * @param messageRoute
     */
    void publish(com.risen.base.event.driven.core.Event event, MessageRoute messageRoute);
}
