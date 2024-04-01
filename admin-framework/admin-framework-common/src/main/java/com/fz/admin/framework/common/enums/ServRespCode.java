package com.fz.admin.framework.common.enums;

import lombok.Getter;

@Getter
public enum ServRespCode {


    SUCCESS(0, "success"),

    USER_ERROR(10001, "用户端错误"),
    USER_REGISTER_ERROR(11000, "用户注册错误"),
    USER_REGISTER_NOT_ALLOW_PRIVACY_POLICY(11001, "用户未同意隐私政策"),
    USER_REGISTER_USERNAME_EXIST(11002, "用户名已存在"),

    USER_LOGIN_ERROR(12000, "用户登录异常"),
    UNAUTHORIZED(12001, "用户未登录"),
    USER_LOGIN_ACCOUNT_NOT_EXIST(12002, "用户账户不存在"),
    USER_LOGIN_PASSWORD_WRONG(12003, "用户密码错误"),
    EMAIL_EXISTED(12004, "邮箱已存在"),
    MOBILE_EXISTED(12005, "手机号已存在"),
    USER_NOT_EXIST(12006, "用户不存在"),
    PASSWORD_FAIL(12007, "用户密码校验失败"),

    // 权限
    ACCESS_FORBIDDEN(13000, "访问权限异常"),

    // 请求
    REQUEST_PARAMETER_ERROR(14000, "参数错误"),


    REQUEST_NOT_FOUND_ERROR(15000, "资源不存在"),

    REQUEST_METHOD_ERROR(15001, "请求方法异常"),

    SERVER_INTERNAL_ERROR(90000, "系统内部异常");


    private final Integer code;
    private final String msg;

    ServRespCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
