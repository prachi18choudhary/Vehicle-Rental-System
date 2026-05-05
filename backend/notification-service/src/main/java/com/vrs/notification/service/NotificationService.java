package com.vrs.notification.service;

import com.vrs.notification.dto.NotificationDto;
import com.vrs.notification.entity.Notification;
import com.vrs.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;
    private final RealtimeNotifier realtime;
    private final EmailService email;

    @Transactional
    public NotificationDto createAndDispatch(Long userId, String userEmail, String type,
                                             String title, String message,
                                             String emailTemplate, Map<String, Object> emailVariables) {
        Notification n = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .channel("EMAIL,WS")
                .read(false)
                .build();
        n = repo.save(n);
        NotificationDto dto = toDto(n);
        realtime.pushToUser(userId, dto);
        if (emailTemplate != null && userEmail != null && !userEmail.isBlank()) {
            email.sendHtml(userEmail, title, emailTemplate, emailVariables);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> list(Long userId, Pageable pageable) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return repo.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public int markAllRead(Long userId) {
        return repo.markAllRead(userId);
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(n.getId(), n.getUserId(), n.getType(),
                n.getTitle(), n.getMessage(), n.getChannel(), n.isRead(), n.getCreatedAt());
    }
}
