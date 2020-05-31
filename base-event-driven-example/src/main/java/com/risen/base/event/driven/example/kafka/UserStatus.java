package com.risen.base.event.driven.example.kafka;

/**
 * 用户状态
 *
 * @author mengxr
 */
public interface UserStatus {
    /**
     * 创建用户
     */
    String CREATE_USER_EVENT = "create_user";
    /**
     * 修改用户
     */
    String MODIFY_USER_EVENT = "modify_user";
}
