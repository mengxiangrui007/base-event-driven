package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventSub;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mengxr
 */
public class DefaultEventDrivenSubHookManager implements EventDrivenSubHookManager {

    private List<EventDrivenSubHook> eventDrivenSubHookList = new ArrayList<>();

    @Override
    public List<EventDrivenSubHook> allSubHook() {
        return eventDrivenSubHookList;
    }

    @Override
    public void addEventDrivenSubHook(EventDrivenSubHook eventDrivenSubHook) {
        eventDrivenSubHookList.add(eventDrivenSubHook);
    }

    @Override
    public void addEventDrivenSubHooks(List<EventDrivenSubHook> eventDrivenSubHooks) {
        eventDrivenSubHookList.addAll(eventDrivenSubHooks);
    }

    @Override
    public void removeEventDrivenSubHook(EventDrivenSubHook eventDrivenSubHook) {
        eventDrivenSubHookList.remove(eventDrivenSubHook);
    }

    @Override
    public void executeSubSuccess(EventSub eventSub) {
        eventDrivenSubHookList.forEach(eventDrivenSubHook -> {
            eventDrivenSubHook.subSuccess(eventSub);
        });
    }

    @Override
    public void executeSubError(EventSub eventSub, Exception e) {
        eventDrivenSubHookList.forEach(eventDrivenSubHook -> {
            eventDrivenSubHook.subError(eventSub, e);
        });
    }
}
