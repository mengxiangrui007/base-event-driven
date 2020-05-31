package com.risen.base.event.driven.core;

import com.risen.base.event.driven.core.model.EventSub;

import java.util.List;
import java.util.Set;

/**
 * @author mengxr
 */
public interface EventDispatcher {

    void dispatch(EventSub eventSub);

    Set<String> getEventType();

    List<DomainEventHandler> getEventHandlers();
}
