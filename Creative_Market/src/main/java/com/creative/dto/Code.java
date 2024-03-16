package com.creative.dto;

public class  Code {

    public static Integer NORMAL = 200;
    //系统异常
    public static Integer SYSTEM_ERR = 500;
    //客户端发送的请求在语法上有错误
    public static Integer SYNTAX_ERROR = 400;
    //权限不足（无token）
    public static Integer INSUFFICIENT_PERMISSIONS = 401;
}
