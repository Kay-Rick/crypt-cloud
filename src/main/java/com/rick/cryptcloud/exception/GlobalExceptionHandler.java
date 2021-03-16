/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-31 13:11:50
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:19:57
 * @Description: 全局异常处理类
 */
package com.rick.cryptcloud.exception;

import com.rick.cryptcloud.controller.ExceptionController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(assignableTypes = {ExceptionController.class})
@ResponseBody
public class GlobalExceptionHandler {

    ErrorResponse illegalErrorResponse = new ErrorResponse(new IllegalArgumentException("参数错误"));
    ErrorResponse resourceNotFoundErrorResponse = new ErrorResponse(new ResourceNotFoundException("Sorry, the resource not found!"));

    // 拦截所有异常, 这里只是为了演示，一般情况下一个方法特定处理一种异常
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.status(400).body(illegalErrorResponse);
        } else if (e instanceof ResourceNotFoundException) {
            return ResponseEntity.status(404).body(resourceNotFoundErrorResponse);
        }
        return null;
    }
    
}
