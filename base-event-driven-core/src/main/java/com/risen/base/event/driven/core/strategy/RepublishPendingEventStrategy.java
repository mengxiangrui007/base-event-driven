package com.risen.base.event.driven.core.strategy;

import com.risen.base.event.driven.core.EventStatus;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.model.EventPub;

import java.time.OffsetDateTime;
import java.util.Set;


public enum RepublishPendingEventStrategy implements BatchFetchEventStrategy {
    SINGLETON;

    @Override
    public Set<EventPub> execute(EventPubMapper mapper, String eventGroup, String eventService) {
        // 取出3秒前已经发送过至队列但是没有收到ack请求的消息，并进行重试
        return mapper.selectLimitedEntityByEventTypeBeforeTheSpecifiedUpdateTime(EventStatus.PENDING.getType(), eventGroup,
                eventService, 300, OffsetDateTime.now().minusSeconds(3));
    }
}
