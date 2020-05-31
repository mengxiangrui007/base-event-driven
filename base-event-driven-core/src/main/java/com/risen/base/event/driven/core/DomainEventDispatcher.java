package com.risen.base.event.driven.core;

import com.risen.base.event.driven.core.model.EventSub;
import com.risen.base.event.driven.core.utils.EventTypeUtils;
import com.risen.base.event.driven.core.utils.Jacksons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 领域事件调度器
 *
 * @author mengxr
 */
public class DomainEventDispatcher implements com.risen.base.event.driven.core.EventDispatcher, InitializingBean, ApplicationContextAware {

    private ConcurrentHashMap<String, List<com.risen.base.event.driven.core.DomainEventHandler>> domainEventTypeHandlers = new ConcurrentHashMap<>();

    private ApplicationContext applicationContext;

    private ConcurrentHashMap<String, ResolvableType> resolvableTypes = new ConcurrentHashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(com.risen.base.event.driven.core.DomainEventDispatcher.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    @Override
    public void dispatch(EventSub eventSub) {
        String eventType = eventSub.getEventType();
        List<com.risen.base.event.driven.core.DomainEventHandler> eventHandlers = domainEventTypeHandlers.get(eventType);
        if (!CollectionUtils.isEmpty(eventHandlers)) {
            eventHandlers.forEach(eventHandler -> {
                ResolvableType resolvableType = resolvableTypes.get(eventHandler.getClass().getName());
                Object target = Jacksons.readValue(eventSub.getPayloadData(), resolvableType.getRawClass());
                eventHandler.handleBiz(target, eventSub.getEventContextKey());
            });
        } else {
            LOGGER.warn("event id {} eventType {} no match handler.", eventSub.getEventId(), eventType);
        }
    }

    @Override
    public Set<String> getEventType() {
        return domainEventTypeHandlers.keySet();
    }

    @Override
    public List<com.risen.base.event.driven.core.DomainEventHandler> getEventHandlers() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String[] beanNames = applicationContext.getBeanNamesForType(com.risen.base.event.driven.core.DomainEventHandler.class);
        if (beanNames != null && beanNames.length > 0) {
            for (String beanName : beanNames) {
                com.risen.base.event.driven.core.DomainEventHandler domainEventHandler = applicationContext.getBean(beanName, com.risen.base.event.driven.core.DomainEventHandler.class);
                initEventType(domainEventHandler);
                initEventHandlerGenerics(domainEventHandler);
            }
        }
    }

    /**
     * 初始化EventType
     *
     * @param domainEventHandler
     */
    private void initEventType(com.risen.base.event.driven.core.DomainEventHandler domainEventHandler) {
        Class<? extends com.risen.base.event.driven.core.DomainEventHandler> aClass = domainEventHandler.getClass();
        com.risen.base.event.driven.core.EventType eventType = aClass.getAnnotation(com.risen.base.event.driven.core.EventType.class);
        if (Objects.nonNull(eventType)) {
            String type = EventTypeUtils.generateEventType(eventType.orgName(), eventType.serverName(), eventType.bizType());
            List<com.risen.base.event.driven.core.DomainEventHandler> eventHandlers = domainEventTypeHandlers.get(type);
            if (!CollectionUtils.isEmpty(eventHandlers)) {
                eventHandlers.add(domainEventHandler);
            } else {
                List<com.risen.base.event.driven.core.DomainEventHandler> domainEventHandlerList = new ArrayList<>(1);
                domainEventHandlerList.add(domainEventHandler);
                domainEventTypeHandlers.put(type, domainEventHandlerList);
            }
        } else {
            throw new com.risen.base.event.driven.core.EventDrivenException(aClass.getName() + " no set @EventType annotation");
        }
    }

    /**
     * 初始化Handler范型类
     *
     * @param domainEventHandler
     */
    private void initEventHandlerGenerics(com.risen.base.event.driven.core.DomainEventHandler domainEventHandler) {
        ResolvableType resolvableType = ResolvableType.forInstance(domainEventHandler).as(com.risen.base.event.driven.core.DomainEventHandler.class).getGeneric();
        if (resolvableType.isAssignableFrom(Type.class)) {
            resolvableTypes.put(domainEventHandler.getClass().getName(), ResolvableType.forClass(Object.class));
        } else {
            resolvableTypes.put(domainEventHandler.getClass().getName(), resolvableType);
        }
    }
}
