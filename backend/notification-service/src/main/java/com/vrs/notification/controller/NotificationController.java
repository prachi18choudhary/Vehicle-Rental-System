package com.vrs.notification.controller;

import com.vrs.common.security.AuthHeaders;
import com.vrs.notification.dto.NotificationDto;
import com.vrs.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/me")
    public ResponseEntity<Page<NotificationDto>> myNotifications(
            @RequestHeader(AuthHeaders.USER_ID) Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(service.list(userId, pageable));
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(
            @RequestHeader(AuthHeaders.USER_ID) Long userId) {
        return ResponseEntity.ok(Map.of("count", service.unreadCount(userId)));
    }

    @PostMapping("/me/read-all")
    public ResponseEntity<Map<String, Integer>> readAll(
            @RequestHeader(AuthHeaders.USER_ID) Long userId) {
        return ResponseEntity.ok(Map.of("updated", service.markAllRead(userId)));
    }
}
