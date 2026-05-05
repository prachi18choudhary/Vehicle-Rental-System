package com.vrs.auth.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        String fullName,
        List<String> roles
) {}
