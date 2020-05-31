package com.risen.base.event.driven.adapter.config;

import com.risen.base.event.driven.adapter.kafka.KafkaMessageTemplate;
import com.risen.base.event.driven.adapter.kafka.KafkaProducerListener;
import com.risen.base.event.driven.core.MessageTemplate;
import com.risen.base.event.driven.core.hook.EventDrivenPubHookManager;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author mengxr
 */
@Configuration
public class EventDrivenAdapterConfiguration {

    /**
     * Kafka适配器
     */
    @ConditionalOnClass(KafkaTemplate.class)
    @Configuration
    @EnableConfigurationProperties(KafkaProperties.class)
    protected static class EventDrivenKafkaAdapterConfiguration {
        @Bean
        public MessageTemplate messageTemplate(KafkaTemplate kafkaTemplate) {
            return new KafkaMessageTemplate(kafkaTemplate);
        }

        @Bean
        public KafkaProducerListener kafkaProducerListener(EventPubMapper eventPubMapper, EventDrivenPubHookManager eventDrivenPubHookManager) {
            return new KafkaProducerListener(eventPubMapper, eventDrivenPubHookManager);
        }
    }

}