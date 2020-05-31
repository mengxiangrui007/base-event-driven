package com.risen.base.event.driven.core;

import com.risen.base.event.driven.core.config.EventDrivenProperties;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.model.EventPub;
import com.risen.base.event.driven.core.strategy.BatchFetchEventStrategy;
import com.risen.base.event.driven.core.strategy.PublishNewEventStrategy;
import com.risen.base.event.driven.core.strategy.RepublishPendingEventStrategy;
import com.risen.base.event.driven.core.utils.EventTypeUtils;
import com.risen.base.event.driven.core.utils.Jacksons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 事件发布
 *
 * @author mengxr
 * @author liuhonglai
 */
public class EventDrivenPublisher implements ApplicationContextAware {

    private EventPubMapper eventPubMapper;

    private com.risen.base.event.driven.core.MessageTemplate messageTemplate;

    private EventDrivenProperties eventDrivenProperties;

    private com.risen.base.event.driven.core.EventDrivenPublisher publisherProxy;

    private ApplicationContext applicationContext;

    /**
     * 默认服务名称
     */
    private static final String DEFAULT_SERVER_NAME = "application";

    public EventDrivenPublisher(EventPubMapper eventPubMapper, com.risen.base.event.driven.core.MessageTemplate messageTemplate, EventDrivenProperties eventDrivenProperties) {
        this.eventPubMapper = eventPubMapper;
        this.messageTemplate = messageTemplate;
        this.eventDrivenProperties = eventDrivenProperties;
    }

    public void init() {
        this.publisherProxy = applicationContext.getBean(com.risen.base.event.driven.core.EventDrivenPublisher.class);
    }

    /**
     * 消息路由缓存
     */
    private static final ConcurrentMap<String, MessageRoute> REGISTRY = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(com.risen.base.event.driven.core.EventDrivenPublisher.class);

    /**
     * 所有的业务类型都必须先注册exchange与routeKey才能使用，而不是将exchange与routeKey持久化，浪费大量磁盘空间。
     */
    public static void registerType(String bizType, MessageRoute messageRoute) {
        Assert.notNull(bizType, "register type bizType not Null");
        Assert.notNull(messageRoute, "register type messageRoute not Null");
        REGISTRY.put(bizType, messageRoute);
    }

    public static void registerType(String[] bizTypes, MessageRoute messageRoute) {
        Assert.notNull(bizTypes, "register type bizTypes not Null");
        Assert.notNull(messageRoute, "register type messageRoute not Null");
        for (String bizType : bizTypes) {
            registerType(bizType, messageRoute);
        }
    }

    /**
     * 判断业务类型是否有被注册
     */
    public static boolean includesBizType(String bizType) {
        Assert.notNull(bizType, "REGISTRY check bizType eventType not Null");
        return REGISTRY.containsKey(bizType);
    }

    /**
     * 根据传入的事件类型取出所设定的exchange与routeKey
     */
    public static MessageRoute getBizTypeMessageRoute(String eventType) {
        Assert.notNull(eventType, "getMessageRoute  eventType not Null");
        return REGISTRY.get(eventType);
    }

    /**
     * 校验业务类型是否被注册
     *
     * @param bizType
     */
    private void throwIfNotIncluded(String bizType) {
        Assert.notNull(bizType, "throwIfNotIncluded bizType not Null");
        if (!includesBizType(bizType)) {
            throw new IllegalArgumentException("该业务类型尚未注册消息通道");
        }
    }

    /**
     * 扫描定量的NEW事件，发布至Broker之后更新为PENDING
     * 暂时不需要此方法
     */
    public void fetchAndPublishEventInNewStatus() {
        fetchAndPublishToBroker(PublishNewEventStrategy.SINGLETON);
    }

    /**
     * @TODO 此次并发量大与正常业务发送有并发问题, 先注释后期有需要需要修复这个bug
     * 扫面定量的PENDING事件并重新发布至Broker，意在防止实例因为意外宕机导致basic.return和basic.ack的状态丢失。
     */
//    @Scheduled(fixedRate = EventDrivenContent.PUBLISH_PENDING_STATUS_FIXEDRATE)
    public void fetchAndRepublishEventInPendingStatus() {
        fetchAndPublishToBroker(RepublishPendingEventStrategy.SINGLETON);
    }

    /**
     * 按照指定的策略将指定状态的事件(通常为NEW与PENDING)发布至Broker
     * TODO 任务分片策略。暂时使用乐观锁避免重复发送的问题
     */
    public void fetchAndPublishToBroker(BatchFetchEventStrategy fetchStrategy) {
        Assert.notNull(fetchStrategy, "BatchFetchEventStrategy not Null");
        final Set<EventPub> events = fetchStrategy.execute(eventPubMapper,
                eventDrivenProperties.getServerGroup(), eventDrivenProperties.getServerName());
        doPublish(events);
    }

    /**
     * 发布事件
     * 注意调用代理方法
     */
    public void doPublish(Collection<EventPub> events) {
        for (EventPub eventPub : events) {
            publisherProxy.doPublish(eventPub);
        }
    }

    /**
     * 发布事件
     * 以一条记录、一条消息作为最小事务单元。解决两个问题：
     * 1. 批量发消息的长事务占用数据库连接过长
     * 2. 长事务的提交有可能覆盖终态数据(比如消息发送监听器已将数据置为Done状态，长事务提交后又置为Pending)
     * 内部逻辑：
     * 1. 更新事件状态为 {@link com.risen.base.event.driven.core.EventStatus#PENDING}
     * 2. 调用具体的消息队列发布事件，并由对应的消息队列监听器负责处理事件发布结果
     * 3. 发送事件前出现异常将直接标记失败
     *
     * @param eventPub 待发送事件
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 10)
    public void doPublish(EventPub eventPub) {
        final String eventType = eventPub.getEventType();
        String bizType = EventTypeUtils.getEventBizType(eventType);
        if (includesBizType(bizType)) {
            // 发送
            final MessageRoute route = getBizTypeMessageRoute(bizType);
            // DTO转换
            com.risen.base.event.driven.core.Event event = convertEvent(eventPub);
            // 更新状态为'处理中'顺便刷新一下update_time
            eventPub.setEventStatus(com.risen.base.event.driven.core.EventStatus.PENDING.getType());
            if (eventPubMapper.updateByPrimaryKeySelectiveWithOptimisticLock(eventPub) > 0) {
                // 正式发送至Broker
                messageTemplate.publish(event, route);
            }
        } else {
            // 将event status置为FAILED，等待人工处理
            eventPub.setEventStatus(com.risen.base.event.driven.core.EventStatus.FAILED.getType());
            if (eventPubMapper.updateByPrimaryKeySelectiveWithOptimisticLock(eventPub) > 0) {
                LOGGER.warn("事件尚未注册不能被发送至Broker, id: {}, eventId: {}，目前已将该事件置为FAILED，待审查过后人工将状态校正"
                        , eventPub.getId(), eventPub.getEventId());
            }
        }
    }

    /**
     * 转换为事件
     *
     * @param eventPub
     * @return
     */
    private com.risen.base.event.driven.core.Event convertEvent(EventPub eventPub) {
        final com.risen.base.event.driven.core.Event dto = new com.risen.base.event.driven.core.Event();
        dto.setEventId(eventPub.getEventId());
        dto.setEventType(eventPub.getEventType());
        dto.setEventOccurTime(eventPub.getEventOccurTime());
        dto.setEventContextKey(eventPub.getEventContextKey());
        Map<String, String> metadata = new HashMap<>();
        metadata.put("eventGroup", eventPub.getEventGroup());
        dto.setMetadata(metadata);
        String payloadData = eventPub.getPayloadData();
        dto.setPayloadData(Jacksons.readValue(payloadData, Object.class));
        return dto;
    }


    /**
     * 消息落地 && 发布
     *
     * @param payload         消息体
     * @param bizType         业务类型
     * @param eventContextKey 聚合根
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int persistPublishMessage(Object payload, String bizType, String eventContextKey) {
        Assert.notNull(eventContextKey, "persistPublishMessage eventContextKey not Null");
        return persist(payload, bizType, eventContextKey);
    }

    /**
     * 消息落地 && 发布 无序消息
     *
     * @param payload 消息体
     * @param bizType 业务类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int persistPublishMessage(Object payload, String bizType) {
        return persist(payload, bizType, null);
    }

    /**
     * 持久化
     *
     * @param payload
     * @param bizType
     * @param eventContextKey
     * @return
     */
    private int persist(Object payload, String bizType, String eventContextKey) {
        Assert.notNull(payload, "persistPublishMessage payload not Null");
        Assert.notNull(bizType, "persistPublishMessage bizType not Null");
        // 严格控制发往Broker的事件类型
        throwIfNotIncluded(bizType);
        final EventPub publisher = new EventPub();
        publisher.setEventId(UUID.randomUUID().toString());
        publisher.setEventOccurTime(new Date());
        String serverName = eventDrivenProperties.getServerName();
        if (StringUtils.isEmpty(serverName) || DEFAULT_SERVER_NAME.equals(serverName)) {
            throw new IllegalArgumentException("服务名称不得为空 请设置spring.application.name属性");
        }
        publisher.setEventGroup(eventDrivenProperties.getServerGroup());
        publisher.setEventService(serverName);
        publisher.setEventStatus(com.risen.base.event.driven.core.EventStatus.PENDING.getType());
        publisher.setEventContextKey(eventContextKey);
        String eventType = EventTypeUtils.generateEventType(eventDrivenProperties.getOrgName()
                , eventDrivenProperties.getServerName(), bizType);
        publisher.setEventType(eventType);
        publisher.setPayloadData(Jacksons.parse(payload));
        publisher.setLockVersion(com.risen.base.event.driven.core.EventDrivenContent.EVENT_LOCK_VERSION);
        // 事务提交后直接发送消息
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                publisherProxy.doPublish(publisher);
            }
        });
        return eventPubMapper.insertSelective(publisher);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
