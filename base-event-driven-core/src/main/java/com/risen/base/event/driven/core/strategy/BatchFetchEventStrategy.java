package com.risen.base.event.driven.core.strategy;

import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.model.EventPub;

import java.util.Set;

/**
 * 批量事件发生策略
 */
public interface BatchFetchEventStrategy {
    /**
     * @param mapper       发布Mapper
     * @param eventGroup   事件分组
     * @param eventService 事件服务
     * @return
     */
    Set<EventPub> execute(EventPubMapper mapper, String eventGroup, String eventService);
}