package com.itheima.reggie.common;

import com.itheima.reggie.web.R;
import com.itheima.reggie.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/9 10:16
 */
@RestControllerAdvice(annotations = {RestController.class,Controller.class})
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R exceptionHandler(BusinessException e){
        return R.error(e.getMessage());
    }
}
