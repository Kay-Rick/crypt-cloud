package com.rick.cryptcloud.Enum;

public enum ResultEnum {

    SUCCESS(200, "操作成功"),

    FAILED(404, "响应失败"),

    ERROR(500, "未知错误");

    private int code;

    private String msg;

    ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResultEnum getByCode(int code) {
        for (ResultEnum resultEnum : ResultEnum.values()) {
            if (code == resultEnum.getCode()) {
                return resultEnum;
            }
        }
        return null;
    }
    
    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
