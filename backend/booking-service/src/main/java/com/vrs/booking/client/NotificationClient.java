package com.vrs.booking.client;

import com.vrs.common.event.BookingCreatedEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/internal/booking-event")
    void sendBookingEvent(@RequestBody BookingCreatedEvent event, @RequestParam(value = "routingKey", required = false) String routingKey);
}
