package com.risen.base.event.driven.core;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

/**
 * 领域事件
 *
 * @author mengxr
 */
public class DomainEvent extends ApplicationEvent {
    /**
     * 消息体
     */
    private Object payload;
    /**
     * 业务类型
     */
    private String businessType;
    /**
     * 聚合根
     */
    private String eventContextKey;
    /**
     * 是否为远程事件 默认是
     */
    private Boolean remoteEvent = Boolean.TRUE;


    public DomainEvent(Object source, Object payload, String businessType, String eventContextKey) {
        super(source);
        Assert.notNull(payload, "领域事件消息体不得为空");
        Assert.notNull(businessType, "领域事件业务类型不得为空");
        Assert.notNull(eventContextKey, "领域事件聚合根不得为空");
        this.payload = payload;
        this.businessType = businessType;
        this.eventContextKey = eventContextKey;
    }

    public DomainEvent(Object source, Object payload, String businessType, String eventContextKey, Boolean remoteEvent) {
        super(source);
        Assert.notNull(payload, "领域事件消息体不得为空");
        Assert.notNull(businessType, "领域事件业务类型不得为空");
        Assert.notNull(eventContextKey, "领域事件聚合根不得为空");
        Assert.notNull(remoteEvent, "领域事件远程事件不得为空");
        this.payload = payload;
        this.businessType = businessType;
        this.eventContextKey = eventContextKey;
        this.remoteEvent = remoteEvent;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getEventContextKey() {
        return eventContextKey;
    }

    public void setEventContextKey(String eventContextKey) {
        this.eventContextKey = eventContextKey;
    }

    public Boolean getRemoteEvent() {
        return remoteEvent;
    }

    public void setRemoteEvent(Boolean remoteEvent) {
        this.remoteEvent = remoteEvent;
    }
}
