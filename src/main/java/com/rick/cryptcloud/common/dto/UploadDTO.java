package com.rick.cryptcloud.common.dto;

import com.rick.cryptcloud.common.Enum.DTOEnum;

public class UploadDTO {
    
    public Integer code;

    public String message;

    public UploadDTO() {
        this(DTOEnum.SUCCESS);
    }

    public UploadDTO(DTOEnum result) {
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
        return "{" +
            " code='" + getCode() + "'" +
            ", message='" + getMessage() + "'" +
            "}";
    }


}
