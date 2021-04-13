/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-29 23:14:12
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 21:42:13
 * @Description: 编写一个拦截器实现HandlerInterceptor接口；
 *              拦截器注册到容器中(实现WebMvcConfigurer的addInterceptors)
 *              指定拦截规则
 */
package com.rick.cryptcloud.config;

import com.rick.cryptcloud.interceptor.LoginInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterCeptorConfig implements WebMvcConfigurer{
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /aaa 的所有请求都去classpath:/static/ 下面进行匹配
        registry.addResourceHandler("/aaa/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                // 如果是拦截所有，静态资源也会被拦截
                .addPathPatterns("/**")
                // 放行的请求
                .excludePathPatterns("/", "/login", "/css/**", "/fonts/**", "images/**", "/js/**", "/aaa/**");
    }
}