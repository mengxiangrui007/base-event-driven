package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventPub;

/**
 * @author mengxr
 */
public class DefaultEventDrivenPubHook implements EventDrivenPubHook {
    /**
     * 发送成功
     *
     * @param eventPub
     */
    @Override
    public void pubSuccess(EventPub eventPub) {

    }

    /**
     * 发送失败
     *
     * @param eventPub
     * @param e
     */
    @Override
    public void pubError(EventPub eventPub, Exception e) {

    }
}
