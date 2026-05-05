package com.vrs.payment.client;

import com.vrs.common.event.PaymentEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @PostMapping("/bookings/internal/payment-event")
    void sendPaymentEvent(@RequestBody PaymentEvent event);
}
