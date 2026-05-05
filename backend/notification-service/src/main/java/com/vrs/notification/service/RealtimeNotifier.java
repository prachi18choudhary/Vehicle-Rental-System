package com.vrs.notification.service;

import com.vrs.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeNotifier {

    private final SimpMessagingTemplate messaging;

    public void pushToUser(Long userId, NotificationDto notification) {
        try {
            messaging.convertAndSend("/topic/user." + userId, notification);
            log.info("WebSocket push to user {}: {}", userId, notification.title());
        } catch (Exception e) {
            log.warn("WebSocket push failed for user {}: {}", userId, e.getMessage());
        }
    }
}
