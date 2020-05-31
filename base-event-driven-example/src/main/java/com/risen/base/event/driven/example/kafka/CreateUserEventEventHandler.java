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
@EventType(bizType = UserStatus.CREATE_USER_EVENT, serverName = "event-driven")
@Component
public class CreateUserEventEventHandler implements DomainEventHandler<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserEventEventHandler.class);

    @Override
    public void handleBiz(User payloadData, String eventContextKey) {
        LOGGER.info("CreateUserEventEventHandler cusumer event handler payloadData[{}] eventContextKey[{}]", payloadData, eventContextKey);
        throw new RuntimeException("发生异常小心");
    }
}
