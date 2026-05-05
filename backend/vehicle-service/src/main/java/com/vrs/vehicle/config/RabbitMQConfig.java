package com.vrs.vehicle.config;

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
    public Queue vehicleBookingCancelledQueue() {
        return QueueBuilder.durable(RabbitConfig.Q_VEHICLE_BOOKING_CANCELLED).build();
    }

    @Bean
    public Binding bindCancelled(Queue vehicleBookingCancelledQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(vehicleBookingCancelledQueue)
                .to(bookingExchange)
                .with(RabbitConfig.RK_BOOKING_CANCELLED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
