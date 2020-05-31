package com.risen.base.event.driven.core;

import com.risen.base.event.driven.core.config.EventDrivenProperties;
import com.risen.base.event.driven.core.hook.EventDrivenSubHookManager;
import com.risen.base.event.driven.core.mapper.EventSubMapper;
import com.risen.base.event.driven.core.model.EventSub;
import com.risen.base.event.driven.core.utils.Jacksons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 事件订阅
 *
 * @author mengxr
 */
public class EventDrivenSubscriber {

    private EventSubMapper eventSubMapper;

    private com.risen.base.event.driven.core.EventDispatcher eventDispatcher;

    private EventDrivenProperties eventDrivenProperties;

    private EventDrivenSubHookManager eventDrivenSubHookManager;

    public EventDrivenSubscriber(EventSubMapper eventSubMapper, com.risen.base.event.driven.core.EventDispatcher eventDispatcher
            , EventDrivenProperties eventDrivenProperties, EventDrivenSubHookManager eventDrivenSubHookManager) {
        this.eventSubMapper = eventSubMapper;
        this.eventDispatcher = eventDispatcher;
        this.eventDrivenProperties = eventDrivenProperties;
        this.eventDrivenSubHookManager = eventDrivenSubHookManager;
    }

    /**
     * 默认服务名称
     */
    private static final String DEFAULT_SERVER_NAME = "application";

    private static final Logger LOGGER = LoggerFactory.getLogger(com.risen.base.event.driven.core.EventDrivenSubscriber.class);


    @Transactional(rollbackFor = Exception.class)
    public void persistAndHandleMessage(String payload) {
        Assert.notNull(payload, "persistAndHandleMessage businessType not Null");
        com.risen.base.event.driven.core.Event event = Jacksons.readValue(payload, com.risen.base.event.driven.core.Event.class);
        //不包含直接返回
        if (!checkContainEventType(event.getEventType())) {
            return;
        }
        //环境不匹配记录日志并返回，仅配置项 sameEnvLimit 为 true 有效
        if (!checkSameEventEnv(event, eventDrivenProperties)) {
            LOGGER.warn("reject event: {}, reason: not same env. subscriber env: {}", event, eventDrivenProperties.getServerGroup());
            return;
        }
        EventSub eventSub = convertEventSub(event);
        // 非重复消息则执行实际的业务
        if (saveNewEvent(eventSub)) {
            doSubscribe(eventSub);
        }
    }

    /**
     * 订阅业务处理
     *
     * @param eventSub
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    public void doSubscribe(EventSub eventSub) {
        try {
            eventDispatcher.dispatch(eventSub);
            int result = eventSubMapper.updateEventStatusByPrimaryKeyInCasMode(eventSub.getId()
                    , eventSub.getEventStatus(), com.risen.base.event.driven.core.EventStatus.DONE.getType());
            eventDrivenSubHookManager.executeSubSuccess(eventSub);
            LOGGER.info("event id {} result {} execute success.", eventSub.getEventId(), result);
        } catch (Exception e) {
            int result = eventSubMapper.updateEventStatusByPrimaryKeyInCasMode(eventSub.getId()
                    , eventSub.getEventStatus(), com.risen.base.event.driven.core.EventStatus.ERROR.getType());
            eventDrivenSubHookManager.executeSubError(eventSub, e);
            LOGGER.error("event id {} result {} execute error.", eventSub.getEventId(), result, e);
        }
    }

    /**
     * 批量订阅业务处理
     *
     * @param events
     */
    public void doSubscribe(Collection<EventSub> events) {
        for (EventSub eventSub : events) {
            doSubscribe(eventSub);
        }
    }

    /**
     * 保存新事件
     *
     * @param eventSub
     * @return
     */
    private boolean saveNewEvent(EventSub eventSub) {
        try {
            return eventSubMapper.insertSelective(eventSub) > 0;
        } catch (Exception e) {
            LOGGER.warn("insert event sub key warn in processing message eventId:[{}] eventGroup:[{}] eventService:[{}] "
                    , eventSub.getEventId(), eventSub.getEventGroup(), eventSub.getEventService());
            return false;
        }
    }

    private EventSub convertEventSub(com.risen.base.event.driven.core.Event event) {
        final EventSub eventSub = new EventSub();
        eventSub.setEventId(event.getEventId());
        eventSub.setEventType(event.getEventType());
        eventSub.setEventOccurTime(event.getEventOccurTime());
        eventSub.setEventGroup(eventDrivenProperties.getServerGroup());
        eventSub.setEventContextKey(event.getEventContextKey());
        String serverName = eventDrivenProperties.getServerName();
        if (StringUtils.isEmpty(serverName) || DEFAULT_SERVER_NAME.equals(serverName)) {
            throw new IllegalArgumentException("服务名称不得为空 请设置spring.application.name属性");
        }
        eventSub.setEventService(serverName);
        eventSub.setPayloadData(Jacksons.parse(event.getPayloadData()));
        eventSub.setLockVersion(com.risen.base.event.driven.core.EventDrivenContent.EVENT_LOCK_VERSION);
        eventSub.setEventStatus(com.risen.base.event.driven.core.EventStatus.NEW.getType());
        return eventSub;
    }

    /**
     * 校验监听Event Type
     *
     * @param eventType
     * @return
     */
    private boolean checkContainEventType(String eventType) {
        Set<String> eventTypes = eventDispatcher.getEventType();
        return eventTypes.contains(eventType);
    }

    /**
     * 校验生产、消费者的环境是否匹配
     * 环境不匹配记录日志并返回，仅配置项 sameEnvLimit 为 true 有效
     *
     * @param event
     * @param eventDrivenProperties
     * @return
     */
    private boolean checkSameEventEnv(com.risen.base.event.driven.core.Event event, EventDrivenProperties eventDrivenProperties) {
        Boolean sameEnvLimit = eventDrivenProperties.getSameEnvLimit();
        if (sameEnvLimit) {
            Map<String, String> metadata = Optional.ofNullable(event.getMetadata()).orElse(Collections.emptyMap());
            String eventGroup = metadata.getOrDefault("eventGroup", EventDrivenProperties.DEFAULT_EVENT_ENV);
            return eventGroup.equals(eventDrivenProperties.getServerGroup());
        } else {
            return true;
        }
    }
}
