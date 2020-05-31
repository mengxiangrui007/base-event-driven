package com.risen.base.event.driven.core;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

/**
 * 领域事件广播
 *
 * @author mengxr
 */
public class DomainEventApplicationEventMulticaster extends SimpleApplicationEventMulticaster {

    private EventDrivenPublisher eventDrivenPublisher;

    public DomainEventApplicationEventMulticaster(EventDrivenPublisher eventDrivenPublisher) {
        this.eventDrivenPublisher = eventDrivenPublisher;
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        //判断当前是否为领域事件
        if (event instanceof com.risen.base.event.driven.core.DomainEvent) {
            com.risen.base.event.driven.core.DomainEvent domainEvent = (com.risen.base.event.driven.core.DomainEvent) event;
            Boolean remoteEvent = domainEvent.getRemoteEvent();
            if (Boolean.TRUE.equals(remoteEvent)) {
                eventDrivenPublisher.persistPublishMessage(domainEvent.getPayload(), domainEvent.getBusinessType(), domainEvent.getEventContextKey());
            }
        }
        super.multicastEvent(event);
    }
}
