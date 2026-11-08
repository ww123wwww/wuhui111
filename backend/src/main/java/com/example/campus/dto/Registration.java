package com.example.campus.dto;

import java.time.LocalDateTime;

public record Registration(long id, long activityId, long userId, String displayName,
        String username, String title, String location, LocalDateTime startTime,
        String activityStatus, String status, LocalDateTime registeredAt) {}
