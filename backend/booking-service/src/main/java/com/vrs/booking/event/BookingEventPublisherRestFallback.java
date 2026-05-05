package com.vrs.booking.event;

import com.vrs.booking.client.NotificationClient;
import com.vrs.booking.client.VehicleClient;
import com.vrs.common.event.BookingCancelledEvent;
import com.vrs.common.event.BookingCreatedEvent;
import com.vrs.common.event.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "false")
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisherRestFallback implements BookingEventPublisher {

    private final NotificationClient notificationClient;
    private final VehicleClient vehicleClient;

    @Override
    public void publishBookingCreated(BookingCreatedEvent event) {
        log.info("REST Fallback: Publishing booking.created for bookingId={}", event.getBookingId());
        try {
            notificationClient.sendBookingEvent(event, RabbitConfig.RK_BOOKING_CREATED);
        } catch (Exception e) {
            log.error("Failed to send booking event to notification service", e);
        }
    }

    @Override
    public void publishBookingConfirmed(BookingCreatedEvent event) {
        log.info("REST Fallback: Publishing booking.confirmed for bookingId={}", event.getBookingId());
        try {
            notificationClient.sendBookingEvent(event, RabbitConfig.RK_BOOKING_CONFIRMED);
        } catch (Exception e) {
            log.error("Failed to send booking event to notification service", e);
        }
    }

    @Override
    public void publishBookingCancelled(BookingCancelledEvent event) {
        log.info("REST Fallback: Publishing booking.cancelled for bookingId={}", event.getBookingId());
        
        // Notify Notification Service
        try {
            BookingCreatedEvent dummyEvent = new BookingCreatedEvent(
                    event.getBookingId(), event.getUserId(), event.getUserEmail(),
                    event.getVehicleId(), null, null, null, null, null, null);
            notificationClient.sendBookingEvent(dummyEvent, RabbitConfig.RK_BOOKING_CANCELLED);
        } catch (Exception e) {
            log.error("Failed to send booking cancelled event to notification service", e);
        }

        // Notify Vehicle Service to reset status
        try {
            vehicleClient.updateStatus(event.getVehicleId(), "AVAILABLE");
        } catch (Exception e) {
            log.error("Failed to update vehicle status to AVAILABLE", e);
        }
    }
}
