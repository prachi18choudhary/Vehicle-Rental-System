package com.vrs.payment.client;

import com.vrs.common.event.PaymentEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications/internal/payment-event")
    void sendPaymentEvent(@RequestBody PaymentEvent event);
}
