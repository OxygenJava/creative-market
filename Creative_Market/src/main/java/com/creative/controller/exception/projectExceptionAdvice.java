package com.creative.controller.exception;

import com.creative.dto.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
@CrossOrigin
public class projectExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Result doException(){
        return Result.fail();
    }
}
