/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-29 23:16:00
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:21:16
 * @Description: 测试拦截器
 */
package com.rick.cryptcloud.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 目标方法执行之前：是否放行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info("这是登录拦截器: {}，已放行: {}", "LoginInterceptor", request.getRequestURI());
        return true;
    }
    
    /**
     * 目标方法执行之后：是否需要对返回的请求做一些增强
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        log.info("登录拦截器：{}目标方法执行结束", "LoginInterceptor");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        log.info("拦截器: {}执行完成", "LoginInterceptor");
    }
}
