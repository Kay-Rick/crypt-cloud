package com.rick.cryptcloud.DTO;

import com.rick.cryptcloud.Enum.DTOEnum;

public class BasicDTO {

    public Integer code;

    public String message;

    public BasicDTO() {
        this(DTOEnum.SUCCESS);
    }

    public BasicDTO(DTOEnum result) {
        this.code = result.getCode();
        this.message = result.getMsg();
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "{" + " code='" + getCode() + "'" + ", message='" + getMessage() + "'" + "}";
    }

}
