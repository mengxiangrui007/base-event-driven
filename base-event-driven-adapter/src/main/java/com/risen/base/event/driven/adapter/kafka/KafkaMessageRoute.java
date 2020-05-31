package com.risen.base.event.driven.adapter.kafka;

import com.risen.base.event.driven.core.MessageRoute;

/**
 * kafka 消息路由
 *
 * @author mengxr
 */
public class KafkaMessageRoute implements MessageRoute {
    /**
     * Topic
     */
    private String topic;

    public KafkaMessageRoute(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
