package com.vrs.booking.controller;

import com.vrs.booking.entity.Booking;
import com.vrs.booking.entity.BookingStatus;
import com.vrs.booking.event.BookingEventPublisher;
import com.vrs.booking.repository.BookingRepository;
import com.vrs.common.event.BookingCreatedEvent;
import com.vrs.common.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal REST endpoints that replace RabbitMQ listeners when running without RabbitMQ.
 * Called by other services via Feign when app.rabbitmq.enabled=false.
 */
@RestController
@RequestMapping("/bookings/internal")
@RequiredArgsConstructor
@Slf4j
public class BookingInternalController {

    private final BookingRepository bookingRepo;
    private final BookingEventPublisher publisher;

    @PostMapping("/payment-event")
    @Transactional
    public ResponseEntity<Void> onPaymentEvent(@RequestBody PaymentEvent event) {
        log.info("Internal payment event received: bookingId={}, status={}", event.getBookingId(), event.getStatus());
        bookingRepo.findById(event.getBookingId()).ifPresent(b -> {
            if ("SUCCESS".equalsIgnoreCase(event.getStatus())) {
                b.setStatus(BookingStatus.CONFIRMED);
                bookingRepo.save(b);
                publisher.publishBookingConfirmed(toCreatedEvent(b));
            } else if ("FAILED".equalsIgnoreCase(event.getStatus())) {
                b.setStatus(BookingStatus.PAYMENT_FAILED);
                bookingRepo.save(b);
            }
        });
        return ResponseEntity.ok().build();
    }

    private BookingCreatedEvent toCreatedEvent(Booking b) {
        return new BookingCreatedEvent(b.getId(), b.getUserId(), b.getUserEmail(), b.getVehicleId(),
                b.getVehicleName(), b.getPickupAt(), b.getDropoffAt(), b.getPickupLocation(),
                b.getDropoffLocation(), b.getTotalAmount());
    }
}
