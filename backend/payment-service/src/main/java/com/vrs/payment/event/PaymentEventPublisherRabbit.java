package com.vrs.payment.event;

import com.vrs.common.event.PaymentEvent;
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
public class PaymentEventPublisherRabbit implements PaymentEventPublisher {

    private final RabbitTemplate rabbit;

    @Override
    public void publishPaymentEvent(PaymentEvent event, boolean success) {
        String routing = success ? RabbitConfig.RK_PAYMENT_SUCCESS : RabbitConfig.RK_PAYMENT_FAILED;
        rabbit.convertAndSend(RabbitConfig.PAYMENT_EXCHANGE, routing, event);
    }
}
