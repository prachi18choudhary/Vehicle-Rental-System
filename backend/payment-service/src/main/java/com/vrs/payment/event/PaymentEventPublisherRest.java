package com.vrs.payment.event;

import com.vrs.common.event.PaymentEvent;
import com.vrs.payment.client.BookingClient;
import com.vrs.payment.client.NotificationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "false")
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisherRest implements PaymentEventPublisher {

    private final BookingClient bookingClient;
    private final NotificationClient notificationClient;

    @Override
    public void publishPaymentEvent(PaymentEvent event, boolean success) {
        log.info("REST Fallback: Publishing payment event for paymentId={}, status={}", event.getPaymentId(), event.getStatus());
        
        try {
            bookingClient.sendPaymentEvent(event);
        } catch (Exception e) {
            log.error("Failed to send payment event to booking service", e);
        }

        try {
            notificationClient.sendPaymentEvent(event);
        } catch (Exception e) {
            log.error("Failed to send payment event to notification service", e);
        }
    }
}
