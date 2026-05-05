package com.vrs.booking.scheduler;

import com.vrs.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {

    private final BookingService bookingService;

    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void cancelStalePendingPayments() {
        try {
            bookingService.cancelStalePendingPayments(15);
        } catch (Exception e) {
            log.warn("Stale-payment cleanup error: {}", e.getMessage());
        }
    }
}
