/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-30 21:44:43
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:16:52
 * @Description: 测试SpringMVC相关功能
 */
package com.rick.cryptcloud.controller;

import com.rick.cryptcloud.domain.User;
import com.rick.cryptcloud.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getAllUser", method = {RequestMethod.POST, RequestMethod.GET})
    public List<User> getAllUser() {
        log.info("开始查询所有User");
        List<User> users = userService.queryAll();
        log.info("查询结果出参：{}", users.toString());
        return users;
    }

}
