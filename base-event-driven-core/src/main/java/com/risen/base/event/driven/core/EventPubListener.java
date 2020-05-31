package com.risen.base.event.driven.core;

import com.risen.base.event.driven.core.hook.EventDrivenPubHookManager;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.model.EventPub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengxr
 */
public abstract class EventPubListener implements PubListener {

    private EventPubMapper eventPubMapper;

    private EventDrivenPubHookManager eventDrivenPubHookManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPubListener.class);

    public EventPubListener(EventPubMapper eventPubMapper, EventDrivenPubHookManager eventDrivenPubHookManager) {
        this.eventPubMapper = eventPubMapper;
        this.eventDrivenPubHookManager = eventDrivenPubHookManager;
    }


    @Override
    public void success(Event event) {
        EventPub eventPub = queryEventPub(event.getEventId());
        eventPubMapper.updateEventStatusByEventIdKeyInCasMode(event.getEventId(), eventPub.getEventStatus(), EventStatus.DONE.getType());
        eventDrivenPubHookManager.executePubSuccess(eventPub);
        LOGGER.info("send kafka domain eventId:[{}] to broker ack success ", event.getEventId());
    }

    @Override
    public void error(Event event, Exception exception) {
        EventPub eventPub = queryEventPub(event.getEventId());
        eventPubMapper.updateEventStatusByEventIdKeyInCasMode(event.getEventId(), eventPub.getEventStatus(), EventStatus.NOT_FOUND.getType());
        eventDrivenPubHookManager.executePubError(eventPub, exception);
        LOGGER.error("send kafka domain eventId:[{}] to broker ack error ", event.getEventId());
    }

    private EventPub queryEventPub(String eventId) {
        return eventPubMapper.selectByEventId(eventId);
    }
}
