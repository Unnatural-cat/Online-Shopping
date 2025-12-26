package com.example.OnlineShopping.common;

/**
 * 错误码常量
 */
public class ErrorCode {
    // 通用错误码
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int PARAM_ERROR = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_ERROR = 500;

    // 认证相关错误码 (1000-1099)
    public static final int USER_NOT_FOUND = 1001;
    public static final int PASSWORD_ERROR = 1002;
    public static final int USER_DISABLED = 1003;
    public static final int EMAIL_EXISTS = 1004;
    public static final int PHONE_EXISTS = 1005;
    public static final int INVALID_TOKEN = 1006;
    public static final int TOKEN_EXPIRED = 1007;
}

