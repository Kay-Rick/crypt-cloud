package com.rick.cryptcloud.controller;

import com.rick.cryptcloud.DO.User;
import com.rick.cryptcloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @RequestMapping("all")
    public List<User> selectAllUser() {
        log.info("开始处理前端查询所有请求");
        List<User> list = userService.queryAll();
        log.info("处理完毕，返回结果");
        return list;
    }
}
