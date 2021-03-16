/*
 * @Author: Kay_Rick@outlook.com
 * @Date: 2021-01-31 13:04:09
 * @LastEditors: Kay_Rick@outlook.com
 * @LastEditTime: 2021-01-31 13:06:26
 * @Description: 
 */
package com.rick.cryptcloud.exception;

public class ErrorResponse {
    
    private String message;
    private String errorTypeName;

    public ErrorResponse(Exception e) {
        this(e.getClass().getName(), e.getMessage());
    }

    /**
     * 
     * @param message
     * @param errorTypeName
     */
    public ErrorResponse(String message, String errorTypeName) {
        this.message = message;
        this.errorTypeName = errorTypeName;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorTypeName() {
        return this.errorTypeName;
    }

    public void setErrorTypeName(String errorTypeName) {
        this.errorTypeName = errorTypeName;
    }

}
