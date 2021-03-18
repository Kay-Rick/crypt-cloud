package com.rick.cryptcloud.service.Impl;

import com.google.gson.Gson;
import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.dao.UserMapper;
import com.rick.cryptcloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static Gson GSON = new Gson();

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryAll() {
        log.info("开始查询所有用户");
        List<User> results = userMapper.selectAll();
        log.info("查询结果出参：{}", GSON.toJson(results));
        return results;
    }
}
