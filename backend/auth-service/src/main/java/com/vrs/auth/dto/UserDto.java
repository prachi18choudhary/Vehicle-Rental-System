package com.vrs.auth.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        Long id,
        String email,
        String fullName,
        String phone,
        List<String> roles,
        boolean enabled,
        LocalDateTime createdAt
) {}
