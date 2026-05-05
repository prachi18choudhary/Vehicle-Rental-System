package com.vrs.payment.event;

import com.vrs.common.event.PaymentEvent;

public interface PaymentEventPublisher {
    void publishPaymentEvent(PaymentEvent event, boolean success);
}
