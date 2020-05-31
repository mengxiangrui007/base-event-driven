package com.risen.base.event.driven.core;

/**
 * @author mengxr
 */
public interface PubListener {
    void success(Event event);

    void error(Event event, Exception exception);
}
