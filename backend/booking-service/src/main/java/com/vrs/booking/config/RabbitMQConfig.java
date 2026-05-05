package com.vrs.booking.config;

import com.vrs.common.event.RabbitConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    public Queue bookingPaymentQueue() {
        return QueueBuilder.durable(RabbitConfig.Q_BOOKING_PAYMENT).build();
    }

    @Bean
    public Binding paymentSuccessBinding(Queue bookingPaymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(bookingPaymentQueue).to(paymentExchange).with(RabbitConfig.RK_PAYMENT_SUCCESS);
    }

    @Bean
    public Binding paymentFailedBinding(Queue bookingPaymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(bookingPaymentQueue).to(paymentExchange).with(RabbitConfig.RK_PAYMENT_FAILED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter mc) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(mc);
        return t;
    }
}
