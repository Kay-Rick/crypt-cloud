package com.rick.cryptcloud.VO;

import com.rick.cryptcloud.Enum.ResultEnum;

public class ResultVO<T> {
    
    private Integer code;

    private String message;

    private T data;

    // public ResultVO(T data) {
    //     this(ResultEnum.SUCCESS, data);
    // }

    // public ResultVO(ResultEnum result) {
    //     this.code = result.getCode();
    //     this.message = result.getMsg();
    // }

    public ResultVO(ResultEnum result, T data) {
        this.code = result.getCode();
        this.message = result.getMsg();
        this.data = data;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return "{" +
            " code='" + getCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", data='" + getData() + "'" +
            "}";
    }

}
