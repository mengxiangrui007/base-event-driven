package com.risen.base.event.driven.example.kafka;

import com.risen.base.event.driven.core.DomainEventHandler;
import com.risen.base.event.driven.core.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 业务处理
 *
 * @author mengxr
 */
@EventType(bizType = "create_user", serverName = "event-driven")
@Component
public class UserEventEventHandler implements DomainEventHandler<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventEventHandler.class);

    @Override
    public void handleBiz(User payloadData, String eventContextKey) {
        LOGGER.info("UserEventEventHandler payloadData[{}] eventContextKey[{}]", payloadData, eventContextKey);
//        throw new RuntimeException("test Exception");
    }
}
