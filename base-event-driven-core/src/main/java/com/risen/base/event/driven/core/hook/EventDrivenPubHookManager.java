package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventPub;

import java.util.List;

/**
 * @author mengxr
 */
public interface EventDrivenPubHookManager {

    List<EventDrivenPubHook> allPubHook();

    void addEventDrivenPubHook(EventDrivenPubHook eventDrivenPubHook);

    void addEventDrivenPubHooks(List<EventDrivenPubHook> eventDrivenPubHooks);

    void removeEventDrivenPubHook(EventDrivenPubHook eventDrivenPubHook);

    void executePubSuccess(EventPub eventPub);

    void executePubError(EventPub eventPub, Exception e);

}
