package com.vrs.notification.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        Long userId,
        String type,
        String title,
        String message,
        String channel,
        boolean read,
        LocalDateTime createdAt
) {}
