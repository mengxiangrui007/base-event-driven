package com.risen.base.event.driven.core;

import java.lang.annotation.*;

/**
 * @author mengxr
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EventType {
    /**
     * biz名称
     *
     * @return
     */
    String bizType();

    /**
     * 服务名称 为了区分多服务下Biz相同
     *
     * @return
     */
    String serverName();

    /**
     * 组织名称
     *
     * @return
     */
    String orgName() default "risen";
}
