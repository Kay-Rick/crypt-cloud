package com.rick.cryptcloud;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    System.out.println("消息发送成功");
                } else {
                    System.out.println("消息发送失败");
                    System.out.println("错误原因：" + cause);
                }
            }

        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replayCode, String replayText, String exchange, String routingKey) {
                System.out.println("Return执行了");
                System.out.println(message.getBody());
                System.out.println(replayText);
            }
            
        });
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, RK_ROUTING_KEY, "Hello Rick");
    }
}
