package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventPub;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mengxr
 */
public class DefaultEventDrivenPubHookManager implements EventDrivenPubHookManager {

    private List<EventDrivenPubHook> eventDrivenPubHookList = new ArrayList<>();

    @Override
    public List<EventDrivenPubHook> allPubHook() {
        return eventDrivenPubHookList;
    }

    @Override
    public void addEventDrivenPubHook(EventDrivenPubHook eventDrivenPubHook) {
        eventDrivenPubHookList.add(eventDrivenPubHook);
    }

    @Override
    public void addEventDrivenPubHooks(List<EventDrivenPubHook> eventDrivenPubHooks) {
        eventDrivenPubHookList.addAll(eventDrivenPubHooks);
    }

    @Override
    public void removeEventDrivenPubHook(EventDrivenPubHook eventDrivenPubHook) {
        eventDrivenPubHookList.remove(eventDrivenPubHook);
    }

    @Override
    public void executePubSuccess(EventPub eventPub) {
        eventDrivenPubHookList.forEach(eventDrivenPubHook -> {
            eventDrivenPubHook.pubSuccess(eventPub);
        });
    }

    @Override
    public void executePubError(EventPub eventPub, Exception e) {
        eventDrivenPubHookList.forEach(eventDrivenPubHook -> {
            eventDrivenPubHook.pubError(eventPub, e);
        });
    }
}
