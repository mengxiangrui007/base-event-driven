package com.risen.base.event.driven.core.hook;

import com.risen.base.event.driven.core.model.EventSub;

import java.util.List;

/**
 * @author mengxr
 */
public interface EventDrivenSubHookManager {
    List<EventDrivenSubHook> allSubHook();

    void addEventDrivenSubHook(EventDrivenSubHook eventDrivenSubHook);

    void addEventDrivenSubHooks(List<EventDrivenSubHook> eventDrivenSubHooks);

    void removeEventDrivenSubHook(EventDrivenSubHook eventDrivenSubHook);

    void executeSubSuccess(EventSub eventSub);

    void executeSubError(EventSub eventSub, Exception e);
}
