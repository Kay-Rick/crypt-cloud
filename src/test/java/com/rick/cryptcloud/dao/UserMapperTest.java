package com.rick.cryptcloud.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.cryptcloud.DO.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() throws Exception {
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void insertUser() {
    }

    @Test
    public void queryAll() {
        log.info("开始查询所有用户");
        List<User> userList =  userMapper.selectAll();
        log.info("查询结果：{}", GSON.toJson(userList));
    }

}
