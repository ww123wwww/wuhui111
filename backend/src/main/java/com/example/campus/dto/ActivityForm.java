package com.example.campus.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ActivityForm(
        @NotBlank @Size(max=100) String title,
        @NotBlank @Size(max=2000) String description,
        @NotBlank @Size(max=100) String location,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull LocalDateTime deadline,
        @Min(1) @Max(500) int capacity) {}
