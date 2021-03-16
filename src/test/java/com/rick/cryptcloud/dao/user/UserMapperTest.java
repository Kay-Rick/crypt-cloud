package com.rick.cryptcloud.dao.user;

import com.google.gson.Gson;
import com.rick.cryptcloud.domain.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserMapperTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        List<User> list = userMapper.queryAll();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        gson.fromJson(json, List.class);
        log.info("查询用户所有数据为: {}", json);
    }
}
