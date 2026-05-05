package com.vrs.booking.event;

import com.vrs.booking.entity.Booking;
import com.vrs.booking.entity.BookingStatus;
import com.vrs.booking.repository.BookingRepository;
import com.vrs.common.event.BookingCreatedEvent;
import com.vrs.common.event.PaymentEvent;
import com.vrs.common.event.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final BookingRepository bookingRepo;
    private final BookingEventPublisher publisher;

    @RabbitListener(queues = RabbitConfig.Q_BOOKING_PAYMENT)
    @Transactional
    public void onPaymentEvent(PaymentEvent event) {
        log.info("Booking received payment event: bookingId={}, status={}", event.getBookingId(), event.getStatus());
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
    }

    private BookingCreatedEvent toCreatedEvent(Booking b) {
        return new BookingCreatedEvent(b.getId(), b.getUserId(), b.getUserEmail(), b.getVehicleId(),
                b.getVehicleName(), b.getPickupAt(), b.getDropoffAt(), b.getPickupLocation(),
                b.getDropoffLocation(), b.getTotalAmount());
    }
}
