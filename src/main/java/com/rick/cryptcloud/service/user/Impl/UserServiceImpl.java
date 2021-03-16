package com.rick.cryptcloud.service.user.Impl;

import com.google.gson.Gson;
import com.rick.cryptcloud.dao.user.UserMapper;
import com.rick.cryptcloud.domain.User;
import com.rick.cryptcloud.service.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    UserMapper userMapper;

    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public List<User> queryAll() {
        log.info("开始查询全部用户...");
        List<User> res = userMapper.queryAll();
        log.info("查询全部用户结果：{}", new Gson().toJson(res));
        return res;
    }
}
