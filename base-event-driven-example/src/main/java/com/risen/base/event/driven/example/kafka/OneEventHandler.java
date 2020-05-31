package com.risen.base.event.driven.example.kafka;

import com.risen.base.event.driven.core.DomainEventHandler;
import com.risen.base.event.driven.core.EventType;
import org.springframework.stereotype.Component;

/**
 * @author mengxr
 */
@EventType(bizType = "1",serverName = "OneServer")
@Component
public class OneEventHandler implements DomainEventHandler {
    /**
     * 处理业务
     *
     * @param payloadData     内容
     * @param eventContextKey 聚合根 可以为空
     */
    @Override
    public void handleBiz(Object payloadData, String eventContextKey) {
        System.out.println("OneEventHandler");
    }
}
