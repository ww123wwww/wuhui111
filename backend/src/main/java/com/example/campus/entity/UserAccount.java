package com.example.campus.entity;

import com.example.campus.dto.User;

/** 仅认证模块内部使用，不能直接作为 Controller 响应。 */
public record UserAccount(long id, String username, String displayName, String passwordHash, String role) {
    public User toUser() { return new User(id, username, displayName, role); }
}
