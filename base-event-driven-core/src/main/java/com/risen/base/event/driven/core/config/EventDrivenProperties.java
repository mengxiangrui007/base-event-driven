package com.risen.base.event.driven.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 事件驱动
 *
 * @author mengxr
 */
@ConfigurationProperties(EventDrivenProperties.EVENT_DRIVEN_PREFIX)
public class EventDrivenProperties {

    public static final String EVENT_DRIVEN_PREFIX = "event.driven";

    public static final String DEFAULT_EVENT_ENV = "default";

    /**
     * 组织名称
     */
    private String orgName = "risen";
    /**
     * 服务名称
     */
    @Value("${spring.application.name}")
    private String serverName = "application";
    /**
     * 服务分组
     */
    private String serverGroup = DEFAULT_EVENT_ENV;
    /**
     * 事件接收器 是否只接收相同环境 事件发送器 的消息
     */
    private Boolean sameEnvLimit = Boolean.FALSE;
    /**
     * 是否跳过事件发送器
     */
    private Boolean publisherSkip = Boolean.FALSE;
    /**
     * 是否跳过事件接收器
     */
    private Boolean subscriberSkip = Boolean.FALSE;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }

    public Boolean getPublisherSkip() {
        return publisherSkip;
    }

    public void setPublisherSkip(Boolean publisherSkip) {
        this.publisherSkip = publisherSkip;
    }

    public Boolean getSubscriberSkip() {
        return subscriberSkip;
    }

    public void setSubscriberSkip(Boolean subscriberSkip) {
        this.subscriberSkip = subscriberSkip;
    }

    public Boolean getSameEnvLimit() {
        return sameEnvLimit;
    }

    public void setSameEnvLimit(Boolean sameEnvLimit) {
        this.sameEnvLimit = sameEnvLimit;
    }
}
