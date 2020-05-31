package com.risen.base.event.driven.core;

/**
 * 事件驱动常量
 *
 * @author mengxr
 */
public interface EventDrivenContent {
    /**
     * 事件锁默认版本
     */
    int EVENT_LOCK_VERSION = 0;
    /**
     * 扫描定量的NEW事件频率
     */
    int PUBLISH_NEW_STATUS_FIXEDRATE = 700;
    /**
     * 扫面定量的PENDING事件频率
     */
    int PUBLISH_PENDING_STATUS_FIXEDRATE = 5000;
}
