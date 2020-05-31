package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventSub;

/**
 * 事件消费钩子
 *
 * @author mengxr
 */
public interface EventDrivenSubHook {
    /**
     * 接收成功
     */
    void subSuccess(EventSub eventSub);

    /**
     * 接收失败
     */
    void subError(EventSub eventSub, Exception e);
}
