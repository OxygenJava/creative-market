package com.creative.dto;

public class  Code {

    public static Integer NORMAL = 200;
    //系统异常
    public static Integer SYSTEM_ERR = 500;
    //客户端发送的请求在语法上有错误
    public static Integer SYNTAX_ERROR = 400;
    //权限不足（无token）
    public static Integer INSUFFICIENT_PERMISSIONS = 401;

    //查询单个成功
    public static Integer GET_OK = 50001;
    //查询单个失败
    public static Integer GET_ERR = 50000;

    //查询全部成功
    public static Integer SELECTALL_OK = 50011;
    //查询全部失败
    public static Integer SELECTALL_ERR = 50010;

    //添加成功
    public static Integer Add_OK = 50021;
    //添加失败
    public static Integer Add_ERR = 50020;

}
