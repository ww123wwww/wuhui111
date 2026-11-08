package com.example.campus.dto;



/** 返回前端的用户信息，不包含密码或密码哈希。 */
public record User(long id, String username, String displayName, String role) {}
