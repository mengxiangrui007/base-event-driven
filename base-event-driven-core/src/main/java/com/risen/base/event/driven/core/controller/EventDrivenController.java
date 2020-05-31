package com.risen.base.event.driven.core.controller;

import com.risen.base.event.driven.core.EventDrivenPublisher;
import com.risen.base.event.driven.core.EventDrivenSubscriber;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.mapper.EventSubMapper;
import com.risen.base.event.driven.core.model.EventPub;
import com.risen.base.event.driven.core.model.EventSub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @author mengxr
 */
@RestController
@RequestMapping("/event-driven")
public class EventDrivenController {

    @Autowired
    private EventPubMapper eventPubMapper;

    @Autowired
    private EventSubMapper eventSubMapper;

    @Autowired
    private EventDrivenPublisher eventDrivenPublisher;

    @Autowired
    private EventDrivenSubscriber eventDrivenSubscriber;

    @PostMapping("/publish")
    public String publish(@RequestParam("ids") Set<String> ids) {
        if (ids.size() > 100) {
            return "最大支持100条";
        } else {
            Set<EventPub> eventPubs = eventPubMapper.selectByIds(ids);
            if (!CollectionUtils.isEmpty(eventPubs)) {
                eventDrivenPublisher.doPublish(eventPubs);
            }
            return "ok";
        }
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("ids") Set<String> ids) {
        if (ids.size() > 100) {
            return "最大支持100条";
        } else {
            Set<EventSub> eventSubs = eventSubMapper.selectByIds(ids);
            if (!CollectionUtils.isEmpty(eventSubs)) {
                eventDrivenSubscriber.doSubscribe(eventSubs);
            }
            return "ok";
        }
    }

    @PostMapping("/pubEventStatus")
    public List<EventPub> pubEventStatus(@RequestParam("eventStatus") Integer eventStatus) {
        return eventPubMapper.selectByEventStatus(eventStatus);
    }

    @PostMapping("/subEventStatus")
    public List<EventSub> subEventStatus(@RequestParam("eventStatus") Integer eventStatus) {
        return eventSubMapper.selectByEventStatus(eventStatus);
    }
}
