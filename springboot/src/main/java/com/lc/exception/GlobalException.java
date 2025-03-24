package com.lc.exception;

import com.lc.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice("com.lc.controller")
public class GlobalException {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        return Result.error("系统异常!");
    }

    @ExceptionHandler(CustomerException.class)
    @ResponseBody
    public Result customerError(CustomerException e) {
        return Result.error(e.getCode(), e.getMsg());
    }
}
