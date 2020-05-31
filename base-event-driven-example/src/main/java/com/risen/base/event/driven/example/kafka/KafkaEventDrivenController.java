package com.risen.base.event.driven.example.kafka;

import com.risen.base.event.driven.adapter.kafka.KafkaMessageRoute;
import com.risen.base.event.driven.core.DomainEvent;
import com.risen.base.event.driven.core.EventDrivenPublisher;
import com.risen.base.event.driven.core.EventDrivenSubscriber;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Optional;

/**
 * @author mengxr
 */
@RestController
public class KafkaEventDrivenController {

    @Autowired
    private EventDrivenSubscriber eventDrivenSubscriber;

    @Autowired
    private EventDrivenPublisher eventDrivenPublisher;

    private static final String USER_DRIVEN_TOPIC = "user_driven_topic";

    @Autowired
    private ApplicationEventMulticaster applicationEventMulticaster;

    @PostConstruct
    public void postProcess() {
        EventDrivenPublisher.registerType(UserStatus.CREATE_USER_EVENT, new KafkaMessageRoute(USER_DRIVEN_TOPIC));
        EventDrivenPublisher.registerType("test", new KafkaMessageRoute(USER_DRIVEN_TOPIC));

        EventDrivenPublisher.registerType(new String[]{UserStatus.CREATE_USER_EVENT, UserStatus.MODIFY_USER_EVENT}, new KafkaMessageRoute(USER_DRIVEN_TOPIC));
    }

    /**
     * 监听事件
     *
     * @param record
     */
    @KafkaListener(topics = {USER_DRIVEN_TOPIC})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            eventDrivenSubscriber.persistAndHandleMessage((String) message);
        }
    }

    /**
     * 广播事件方式发送
     */
    @GetMapping("publishEvent")
    public void publishEvent() {
        User user = new User(1L, "Shan rui", new Date());
        applicationEventMulticaster.multicastEvent(new DomainEvent(this, user, UserStatus.MODIFY_USER_EVENT, "order:1"));
    }

    /**
     * 持计划方式发送
     */
    @GetMapping("publish")
    public void publish() {
        NewUser user = new NewUser(2L, "Shan rui", new Date(), new Date());
        eventDrivenPublisher.persistPublishMessage(user, UserStatus.CREATE_USER_EVENT);
    }


}
