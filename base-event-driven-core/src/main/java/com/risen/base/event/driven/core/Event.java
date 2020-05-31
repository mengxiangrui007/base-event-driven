package com.risen.base.event.driven.core;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * http://wiki.lianjia.com/pages/viewpage.action?pageId=365305760
 * 事件
 *
 * @author mengxr
 */
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 事件ID 事件标识ID，用于标识单个服务内的唯一一条事件，可通过UUID生成器生成
     */
    private String eventId;
    /**
     * 事件类型 业务标记事件类型，例如订单创建事件可以表示为“order-service:OrderCreated” JSON表示使用ISO8601格式
     */
    private String eventType;
    /**
     * 事件发生时间，使用事件产生的时间 JSON表示使用ISO8601格式
     */
    @com.fasterxml.jackson.annotation.JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date eventOccurTime;

    /**
     * 事件聚合key 事件所属的聚合根对象的类型和ID，消息基础设施保证相同的ContextKey下事件有序
     */
    private String eventContextKey;
    /**
     * 事件扩展信息字段，可用于扩展事件
     */
    private Map<String, String> metadata;
    /**
     * 事件内容
     * 事件载荷信息，需要通过事件消息传递的关键信息
     * <p>
     * 不需要事件载荷时传递null
     * <p>
     * 不建议发布载荷为null的事件
     */
    private Object payloadData;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEventOccurTime() {
        return eventOccurTime;
    }

    public void setEventOccurTime(Date eventOccurTime) {
        this.eventOccurTime = eventOccurTime;
    }

    public String getEventContextKey() {
        return eventContextKey;
    }

    public void setEventContextKey(String eventContextKey) {
        this.eventContextKey = eventContextKey;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Object getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(Object payloadData) {
        this.payloadData = payloadData;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventOccurTime=" + eventOccurTime +
                ", eventContextKey='" + eventContextKey + '\'' +
                ", metadata=" + metadata +
                ", payloadData=" + payloadData +
                '}';
    }
}
