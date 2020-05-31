package com.risen.base.event.driven.adapter.kafka;

import com.risen.base.event.driven.core.Event;
import com.risen.base.event.driven.core.EventPubListener;
import com.risen.base.event.driven.core.hook.EventDrivenPubHookManager;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.utils.Jacksons;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author mengxr
 */
public class KafkaProducerListener extends EventPubListener implements ProducerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageTemplate.class);

    public KafkaProducerListener(EventPubMapper eventPubMapper, EventDrivenPubHookManager eventDrivenPubHookManager) {
        super(eventPubMapper, eventDrivenPubHookManager);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onSuccess(String topic, Integer partition, Object key, Object value, RecordMetadata recordMetadata) {
        if (Objects.nonNull(value)) {
            try {
                Event event = Jacksons.readValue((String) value, Event.class);
                if (Objects.nonNull(event)) {
                    super.success(event);
                }
            } catch (Exception e) {
            }
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onError(String topic, Integer partition, Object key, Object value, Exception exception) {
        if (Objects.nonNull(value)) {
            try {
                Event event = Jacksons.readValue((String) value, Event.class);
                if (Objects.nonNull(event)) {
                    super.error(event, exception);
                }
            } catch (Exception e) {
            }
        }
    }
}
