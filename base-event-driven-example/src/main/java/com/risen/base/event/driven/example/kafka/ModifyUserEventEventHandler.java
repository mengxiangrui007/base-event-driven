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
@EventType(bizType = UserStatus.MODIFY_USER_EVENT,serverName = "event-driven")
@Component
public class ModifyUserEventEventHandler implements DomainEventHandler<User> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyUserEventEventHandler.class);

    @Override
    public void handleBiz(User payloadData, String eventContextKey) {
        LOGGER.info("ModifyUserEventEventHandler cusumer event handler payloadData[{}] eventContextKey[{}]", payloadData, eventContextKey);
    }
}
