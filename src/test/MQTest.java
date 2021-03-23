package com.rick.cryptcloud;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MQTest {
    private static final String EXCHANGE_NAME = "Crypt-Cloud";

    private static final String RK_ROUTING_KEY = "RK";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test1() {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, RK_ROUTING_KEY, "Hello Rick");
    }
}
