package com.example.campus.dto;

import java.time.LocalDateTime;

public record Activity(long id, String title, String description, String location,
        LocalDateTime startTime, LocalDateTime endTime, LocalDateTime deadline,
        int capacity, String status, int registeredCount) {}
