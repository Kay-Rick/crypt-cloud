package com.rick.cryptcloud;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class MailTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender jms;

    @Value("${spring.mail.username}")
    private String from;

    @Test
    public void sendSimpleEmail() {
        String toMailBox = "Kay_Rick@outlook.com";
        System.out.println(from);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(toMailBox);
            message.setSubject("一封简单的邮件");
            message.setText("使用Spring Boot发送简单邮件。");
            log.info("开始给：{}发送邮件", toMailBox);
            jms.send(message);
            log.info("邮件发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("邮件发送给：{}失败, 失败原因：{}",toMailBox, e.getMessage());
        }
    }

}
