package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventPub;

/**
 * 事件发送钩子
 *
 * @author mengxr
 */
public interface EventDrivenPubHook {
    /**
     * 发送成功
     */
    void pubSuccess(EventPub eventPub);

    /**
     * 发送失败
     */
    void pubError(EventPub eventPub, Exception e);
}
