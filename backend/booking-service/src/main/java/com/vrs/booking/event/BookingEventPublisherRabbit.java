package com.vrs.booking.event;

import com.vrs.common.event.BookingCancelledEvent;
import com.vrs.common.event.BookingCreatedEvent;
import com.vrs.common.event.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class BookingEventPublisherRabbit implements BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishBookingCreated(BookingCreatedEvent event) {
        log.info("Publishing booking.created for bookingId={}", event.getBookingId());
        rabbitTemplate.convertAndSend(RabbitConfig.BOOKING_EXCHANGE, RabbitConfig.RK_BOOKING_CREATED, event);
    }

    @Override
    public void publishBookingConfirmed(BookingCreatedEvent event) {
        log.info("Publishing booking.confirmed for bookingId={}", event.getBookingId());
        rabbitTemplate.convertAndSend(RabbitConfig.BOOKING_EXCHANGE, RabbitConfig.RK_BOOKING_CONFIRMED, event);
    }

    @Override
    public void publishBookingCancelled(BookingCancelledEvent event) {
        log.info("Publishing booking.cancelled for bookingId={}", event.getBookingId());
        rabbitTemplate.convertAndSend(RabbitConfig.BOOKING_EXCHANGE, RabbitConfig.RK_BOOKING_CANCELLED, event);
    }
}
