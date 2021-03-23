package com.rick.cryptcloud.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "Crypt-Cloud";
    public static final String QUEUE_RK_NAME = "RK";
    public static final String QUEUE_FK_NAME = "FK";

    @Bean("cloudExchange")
    public Exchange cloudExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean("RKQueue")
    public Queue RKQueue() {
        return QueueBuilder.durable(QUEUE_RK_NAME).build();
    }

    @Bean("FKQueue")
    public Queue FKQueue() {
        return QueueBuilder.durable(QUEUE_FK_NAME).build();
    }

    @Bean
    public Binding bindRK(@Qualifier("RKQueue") Queue rk, @Qualifier("cloudExchange") Exchange exchange) {
        return BindingBuilder.bind(rk).to(exchange).with("RK").noargs();
    }

    @Bean
    public Binding bindFK(@Qualifier("FKQueue") Queue fk, @Qualifier("cloudExchange") Exchange exchange) {
        return BindingBuilder.bind(fk).to(exchange).with("FK").noargs();
    }
}
