package com.risen.base.event.driven.core.strategy;

import com.risen.base.event.driven.core.EventStatus;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.model.EventPub;

import java.util.Set;

/**
 * 发生新事件策略
 */
public enum PublishNewEventStrategy implements BatchFetchEventStrategy {
    SINGLETON;

    @Override
    public Set<EventPub> execute(EventPubMapper mapper, String eventGroup, String eventService) {
        return mapper.selectLimitedEntityByEventType(com.risen.base.event.driven.core.EventStatus.NEW.getType(), eventGroup, eventService, 300);
    }
}