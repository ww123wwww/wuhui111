package com.example.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginForm(
        @NotBlank @Size(max=30) String username,
        @NotBlank @Size(max=64) String password) {}
