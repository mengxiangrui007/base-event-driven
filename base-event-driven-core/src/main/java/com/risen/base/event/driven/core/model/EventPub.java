package com.risen.base.event.driven.core.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
public class EventPub implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 事件ID,用于消费者作去重
     */
    private String eventId;

    /**
     * 事件发生时间
     */
    private Date eventOccurTime;
    /**
     * 事件源服务分组
     */
    private String eventGroup;

    /**
     * 事件源服务
     */
    private String eventService;

    /**
     * 事件状态 -128为未知错误, -3为NOT_FOUND(找不到exchange), -2为NO_ROUTE(找到exchange但是找不到queue), -1为FAILED(如类型尚未注册等的业务失败), 0为NEW(消息落地), 1为PENDING, 2为DONE
     */
    private Integer eventStatus;

    /**
     * 事件聚合根key
     */
    private String eventContextKey;

    /**
     * 事件类型，业务标记事件类型，例如订单创建事件可以表示为“order-service:OrderCreated”
     */
    private String eventType;
    /**
     * 事件扩展信息字段，可用于扩展事件
     */
    private String metadata;
    /**
     * 事件内容
     */
    private String payloadData;

    /**
     * 锁版本号
     */
    private Integer lockVersion;

    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 修改时间
     */
    private Date mtime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getEventOccurTime() {
        return eventOccurTime;
    }

    public void setEventOccurTime(Date eventOccurTime) {
        this.eventOccurTime = eventOccurTime;
    }

    public String getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(String eventGroup) {
        this.eventGroup = eventGroup;
    }

    public String getEventService() {
        return eventService;
    }

    public void setEventService(String eventService) {
        this.eventService = eventService;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getEventContextKey() {
        return eventContextKey;
    }

    public void setEventContextKey(String eventContextKey) {
        this.eventContextKey = eventContextKey;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(String payloadData) {
        this.payloadData = payloadData;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }
}