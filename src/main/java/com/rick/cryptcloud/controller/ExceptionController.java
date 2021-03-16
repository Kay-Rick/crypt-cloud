/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-31 13:13:10
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:15:15
 * @Description: 统一异常处理
 */
package com.rick.cryptcloud.controller;

import com.rick.cryptcloud.exception.ResourceNotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExceptionController {
    
    @GetMapping("/illegalArgumentException")
    public void throwException1() {
        throw new IllegalArgumentException();
    }

    @GetMapping("resourceNotFoundException")
    public void throwException2() {
        throw new ResourceNotFoundException();
    }
}
