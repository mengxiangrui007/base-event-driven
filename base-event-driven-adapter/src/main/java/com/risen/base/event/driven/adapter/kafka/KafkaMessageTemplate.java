package com.risen.base.event.driven.adapter.kafka;

import com.risen.base.event.driven.core.Event;
import com.risen.base.event.driven.core.MessageRoute;
import com.risen.base.event.driven.core.MessageTemplate;
import com.risen.base.event.driven.core.utils.Jacksons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author mengxr
 */
public class KafkaMessageTemplate implements MessageTemplate{

    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageTemplate.class);

    public KafkaMessageTemplate(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发布消息
     *
     * @param event
     * @param messageRoute
     */
    @Override
    public void publish(Event event, MessageRoute messageRoute) {
        LOGGER.info("send kafka domain event:[{}] to broker beginning...", event);
        KafkaMessageRoute kafkaMessageRoute = (KafkaMessageRoute) messageRoute;
        kafkaTemplate.send(kafkaMessageRoute.getTopic(), event.getEventContextKey(), Jacksons.parse(event));
        LOGGER.info("send kafka domain eventId:[{}] to broker done", event.getEventId());
    }
}
