/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-31 13:07:49
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-02-04 22:20:57
 * @Description: 自定义一个RuntimeException
 */
package com.rick.cryptcloud.exception;

public class ResourceNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -7913901328171370151L;
    
    private String message;
    
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
