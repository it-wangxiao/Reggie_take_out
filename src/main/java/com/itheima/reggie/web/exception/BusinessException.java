package com.itheima.reggie.web.exception;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/9 10:05
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
