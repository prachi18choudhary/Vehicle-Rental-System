package com.vrs.notification.config;

import com.vrs.common.event.RabbitConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQConfig {

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(RabbitConfig.BOOKING_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(RabbitConfig.PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationBookingQueue() {
        return QueueBuilder.durable(RabbitConfig.Q_NOTIFICATION_BOOKING).build();
    }

    @Bean
    public Queue notificationPaymentQueue() {
        return QueueBuilder.durable(RabbitConfig.Q_NOTIFICATION_PAYMENT).build();
    }

    @Bean
    public Binding bookingCreatedBinding(Queue notificationBookingQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(notificationBookingQueue).to(bookingExchange).with(RabbitConfig.RK_BOOKING_CREATED);
    }

    @Bean
    public Binding bookingConfirmedBinding(Queue notificationBookingQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(notificationBookingQueue).to(bookingExchange).with(RabbitConfig.RK_BOOKING_CONFIRMED);
    }

    @Bean
    public Binding paymentSuccessBinding(Queue notificationPaymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(notificationPaymentQueue).to(paymentExchange).with(RabbitConfig.RK_PAYMENT_SUCCESS);
    }

    @Bean
    public Binding paymentFailedBinding(Queue notificationPaymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(notificationPaymentQueue).to(paymentExchange).with(RabbitConfig.RK_PAYMENT_FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
