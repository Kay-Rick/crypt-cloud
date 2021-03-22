package com.rick.cryptcloud.Enum;

public enum DTOEnum {
    
    SUCCESS(200, "操作成功"),

    FAILED(404, "响应失败"),

    ERROR(500, "未知错误");

    private int code;

    private String msg;

    DTOEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static DTOEnum getByCode(int code) {
        for (DTOEnum dtoEnum : DTOEnum.values()) {
            if (code == dtoEnum.getCode()) {
                return dtoEnum;
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

