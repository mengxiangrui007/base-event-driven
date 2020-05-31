package com.risen.base.event.driven.core.utils;

import org.springframework.util.Assert;

/**
 * @author mengxr
 */
public class EventTypeUtils {
    /**
     * 生成事件类型
     *
     * @param orgName     组织
     * @param serviceName 服务名称
     * @param bizType     事件类型
     * @return
     */
    public static String generateEventType(String orgName, String serviceName, String bizType) {
        Assert.notNull(orgName, "generateEventType orgName not Null");
        Assert.notNull(serviceName, "generateEventType serviceName not Null");
        Assert.notNull(bizType, "generateEventType bizType not Null");
        return orgName + ":" + serviceName + ":" + bizType;
    }

    /**
     * 获取业务类型
     *
     * @param eventType
     * @return
     */
    public static String getEventBizType(String eventType) {
        Assert.notNull(eventType, "getEventBizType eventType not Null");
        return eventType.split(":")[2];
    }
}
