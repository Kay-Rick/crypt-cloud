package com.rick.cryptcloud.DTO;

import com.rick.cryptcloud.Enum.DTOEnum;

public class FileContentDTO {
   
    public Integer code;

    public String message;
    
    public String content;

    public FileContentDTO(String content) {
        this(DTOEnum.SUCCESS);
        this.content = content;
    }

    public FileContentDTO(DTOEnum result) {
        this.code = result.getCode();
        this.message = result.getMsg();
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return "{" +
            " code='" + getCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", content='" + getContent() + "'" +
            "}";
    }
    
}
