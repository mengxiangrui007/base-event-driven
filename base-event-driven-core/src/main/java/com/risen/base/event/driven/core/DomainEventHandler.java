package com.risen.base.event.driven.core;

/**
 * @author mengxr
 */
public interface DomainEventHandler<T> {
    /**
     * 处理业务
     *
     * @param payloadData     内容
     * @param eventContextKey 聚合根 可以为空
     */
    void handleBiz(T payloadData, String eventContextKey);
}
