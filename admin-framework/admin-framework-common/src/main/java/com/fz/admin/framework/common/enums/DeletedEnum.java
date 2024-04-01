package com.fz.admin.framework.common.enums;

public enum DeletedEnum {


    NOT_DELETED(0, "未删除"),

    DELETED(1, "删除");

    private final Integer code;
    private final String msg;

    DeletedEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static boolean isDeleted(Integer deleted) {
        return DELETED.getCode().equals(deleted);
    }
}
