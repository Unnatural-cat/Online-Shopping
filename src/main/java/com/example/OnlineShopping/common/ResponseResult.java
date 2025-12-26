package com.example.OnlineShopping.common;

import lombok.Data;

/**
 * 统一响应结果类
 */
@Data
public class ResponseResult<T> {
    /**
     * 响应码：0表示成功，非0表示失败
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public ResponseResult() {
    }

    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(0, "操作成功", null);
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(0, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<>(0, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }

    /**
     * 失败响应（默认错误码）
     */
    public static <T> ResponseResult<T> error(String message) {
        return new ResponseResult<>(-1, message, null);
    }
}

