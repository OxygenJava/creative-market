package com.creative.controller.exception;

import com.creative.dto.Code;
import com.creative.dto.Result;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@CrossOrigin
public class projectExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Result doException(Exception exception){
        exception.printStackTrace();
        return Result.fail(Code.SYSTEM_ERR,"系统异常");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return Result.fail(Code.INSUFFICIENT_PERMISSIONS, ex.getMessage());
    }
}
