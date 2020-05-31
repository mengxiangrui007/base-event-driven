package com.risen.base.event.driven.example.simple;

import com.risen.base.event.driven.core.Event;

/**
 * @author mengxr
 */
public class SimpleEvent extends Event {
    @Override
    public String toString() {
        return getClass().toString();
    }
}
