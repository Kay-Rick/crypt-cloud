package com.rick.cryptcloud.dao;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class FKMapperTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private FKMapper fkMapper;

    @Test
    public void test1() {
        System.out.println(GSON.toJson(fkMapper.selectByName("roleD", "Kay.txt")));
    }
}
