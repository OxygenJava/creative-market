package com.creative.dto;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    public static Result success(String message, Object data){
        return new Result(Code.NORMAL,message,data);
    }

    public static Result success(){
        return new Result(Code.NORMAL,"操作成功");
    }

    public static Result success(Object data){
        return new Result(Code.NORMAL,"操作成功",data);
    }

    public static Result success(String message){
        return new Result(Code.NORMAL,message);
    }

    /**
     * 抛出500
     * @return
     */
    public static Result fail(){
        return new Result(Code.SYSTEM_ERR,"系统异常");
    }

    /**
     * 抛出401
     * @param errorMessage
     * @return
     */
    public static Result fail(String errorMessage){
        return new Result(Code.INSUFFICIENT_PERMISSIONS,errorMessage);
    }

    public static Result fail(Integer code,String errorMessage){
        return new Result(code,errorMessage);
    }
}
