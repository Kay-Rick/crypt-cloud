package com.rick.cryptcloud.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JavaMailSender jms;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            log.info("开始给：{}发送邮件", to);
            jms.send(message);
            log.info("邮件发送成功");
        } catch (Exception e) {
            log.error("邮件发送给：{}失败：{}", to, e.getMessage());
        }
    }
}
