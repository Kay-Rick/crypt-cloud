package com.rick.cryptcloud;

import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.common.MailUtils;
import com.rick.cryptcloud.dao.UserMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class MailTest {

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void sendSimpleEmail() {
        String toMailBox = "Kay_Rick@outlook.com";
        mailUtils.sendMail(toMailBox, "一封简单的邮件", "使用Spring Boot发送简单邮件。");
    }

    @Test
    public void test1() {
        String username = "Rick";
        String subject = "Crypt Cloud";
        User user = userMapper.selectByUserName(username);
        String mailContent = "Your Security Key is：" + user.getPrivateKey();
        mailUtils.sendMail(user.getMail(), subject, mailContent);
    }
}
