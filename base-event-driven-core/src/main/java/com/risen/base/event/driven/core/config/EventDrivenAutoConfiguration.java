package com.risen.base.event.driven.core.config;


import com.risen.base.event.driven.core.*;
import com.risen.base.event.driven.core.controller.EventDrivenController;
import com.risen.base.event.driven.core.hook.*;
import com.risen.base.event.driven.core.mapper.EventPubMapper;
import com.risen.base.event.driven.core.mapper.EventSubMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mengxr
 */
@ComponentScan(basePackageClasses = EventDrivenController.class)
@Configuration
@MapperScan("com.risen.base.event.driven.core.mapper")
@EnableScheduling
@EnableConfigurationProperties(EventDrivenProperties.class)
public class EventDrivenAutoConfiguration {

    @ConditionalOnProperty(name = "event.driven.publisherSkip", havingValue = "false", matchIfMissing = true)
    @Bean(initMethod = "init")
    public EventDrivenPublisher eventDrivenPublisher(EventPubMapper eventPubMapper, MessageTemplate messageTemplate, EventDrivenProperties eventDrivenProperties) {
        return new EventDrivenPublisher(eventPubMapper, messageTemplate, eventDrivenProperties);
    }

    @ConditionalOnProperty(name = "event.driven.subscriberSkip", havingValue = "false", matchIfMissing = true)
    @Bean
    public EventDrivenSubscriber eventDrivenSubscriber(EventSubMapper eventSubMapper, EventDispatcher eventDispatcher
            , EventDrivenProperties eventDrivenProperties, EventDrivenSubHookManager eventDrivenSubHookManager) {
        return new EventDrivenSubscriber(eventSubMapper, eventDispatcher
                , eventDrivenProperties, eventDrivenSubHookManager);
    }


    @ConditionalOnBean(EventDrivenPublisher.class)
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(EventDrivenPublisher eventDrivenPublisher) {
        return new DomainEventApplicationEventMulticaster(eventDrivenPublisher);
    }

    @ConditionalOnMissingBean
    @Bean
    public MessageTemplate messageTemplate() {
        return new DefaultMessageTemplate();
    }

    @ConditionalOnMissingBean
    @Bean
    public EventDispatcher dispatcher() {
        return new DomainEventDispatcher();
    }

    @ConditionalOnMissingBean
    @Bean
    public EventDrivenSubHookManager eventDrivenSubHookManager(ObjectProvider<EventDrivenSubHook> objectProvider) {
        EventDrivenSubHookManager defaultEventDrivenSubHookManager = new DefaultEventDrivenSubHookManager();
        defaultEventDrivenSubHookManager.addEventDrivenSubHook(new DefaultEventDrivenSubHook());
        List<EventDrivenSubHook> eventDrivenSubHooks = objectProvider.orderedStream().collect(Collectors.toList());
        defaultEventDrivenSubHookManager.addEventDrivenSubHooks(eventDrivenSubHooks);
        return defaultEventDrivenSubHookManager;
    }

    @ConditionalOnMissingBean
    @Bean
    public EventDrivenPubHookManager eventDrivenPubHookManager(ObjectProvider<EventDrivenPubHook> objectProvider) {
        EventDrivenPubHookManager defaultEventDrivenPubHookManager = new DefaultEventDrivenPubHookManager();
        defaultEventDrivenPubHookManager.addEventDrivenPubHook(new DefaultEventDrivenPubHook());
        List<EventDrivenPubHook> eventDrivenPubHooks = objectProvider.orderedStream().collect(Collectors.toList());
        defaultEventDrivenPubHookManager.addEventDrivenPubHooks(eventDrivenPubHooks);
        return defaultEventDrivenPubHookManager;
    }
}