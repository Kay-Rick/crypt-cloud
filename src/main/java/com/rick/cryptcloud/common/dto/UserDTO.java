package com.rick.cryptcloud.common.dto;

import com.rick.cryptcloud.common.Enum.DTOEnum;

public class UserDTO {

    public Integer code;

    public String message;

    public String username;

    public String rolename;

    public String filename;

    public String operation;

    public UserDTO(DTOEnum result) {
        this.code = result.getCode();
        this.message = result.getMsg();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
