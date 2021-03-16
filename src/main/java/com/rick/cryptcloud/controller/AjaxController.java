/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-31 17:12:17
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:14:47
 * @Description: 测试Ajax功能
 */
package com.rick.cryptcloud.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rick.cryptcloud.domain.User;
import com.rick.cryptcloud.service.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ajax")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AjaxController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/a", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public List<User> ajax1(HttpServletRequest request, HttpServletResponse response, String name) throws IOException {
        log.info("Ajax提交得到参数name：{}", name);
        return userService.queryAll();
    }
}
