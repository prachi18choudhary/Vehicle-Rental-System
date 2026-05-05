package com.vrs.notification.event;

import com.vrs.common.event.BookingCreatedEvent;
import com.vrs.common.event.PaymentEvent;
import com.vrs.common.event.RabbitConfig;
import com.vrs.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class EventListeners {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitConfig.Q_NOTIFICATION_BOOKING)
    public void onBookingEvent(BookingCreatedEvent event,
                               @Header(name = "amqp_receivedRoutingKey", required = false) String routingKey) {
        log.info("Notif received {} for bookingId={}", routingKey, event.getBookingId());

        Map<String, Object> vars = new HashMap<>();
        vars.put("vehicleName", safe(event.getVehicleName()));
        vars.put("bookingId", event.getBookingId());
        vars.put("pickupAt", event.getPickupAt() == null ? "" : event.getPickupAt().format(FMT));
        vars.put("dropoffAt", event.getDropoffAt() == null ? "" : event.getDropoffAt().format(FMT));
        vars.put("pickupLocation", safe(event.getPickupLocation()));
        vars.put("dropoffLocation", safe(event.getDropoffLocation()));
        vars.put("totalAmount", event.getTotalAmount() == null ? "" : event.getTotalAmount().toPlainString());

        boolean confirmed = RabbitConfig.RK_BOOKING_CONFIRMED.equals(routingKey);
        String title = confirmed ? "Booking Confirmed" : "Booking Created";
        String message = confirmed
                ? "Your booking #" + event.getBookingId() + " has been confirmed. Pickup on " + vars.get("pickupAt")
                : "Booking #" + event.getBookingId() + " created. Complete payment to confirm.";
        String template = confirmed ? "booking-confirmed" : "booking-created";
        String type = confirmed ? "BOOKING_CONFIRMED" : "BOOKING_CREATED";

        notificationService.createAndDispatch(event.getUserId(), event.getUserEmail(),
                type, title, message, template, vars);
    }

    @RabbitListener(queues = RabbitConfig.Q_NOTIFICATION_PAYMENT)
    public void onPaymentEvent(PaymentEvent event) {
        log.info("Notif received payment event: paymentId={}, status={}", event.getPaymentId(), event.getStatus());
        boolean success = "SUCCESS".equalsIgnoreCase(event.getStatus());
        Map<String, Object> vars = new HashMap<>();
        vars.put("paymentId", event.getPaymentId());
        vars.put("bookingId", event.getBookingId());
        vars.put("amount", event.getAmount() == null ? "" : event.getAmount().toPlainString());
        vars.put("razorpayPaymentId", safe(event.getRazorpayPaymentId()));

        String title = success ? "Payment Successful" : "Payment Failed";
        String message = success
                ? "Payment of " + vars.get("amount") + " for booking #" + event.getBookingId() + " was successful."
                : "Payment for booking #" + event.getBookingId() + " failed. Please retry.";
        String type = success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED";
        String template = success ? "payment-success" : "payment-failed";

        notificationService.createAndDispatch(event.getUserId(), event.getUserEmail(),
                type, title, message, template, vars);
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
