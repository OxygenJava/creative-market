package com.creative.dto;

import lombok.Data;

@Data
public class loginResult{
    private String message;
    private Integer code;
    private String token;

    public loginResult(String message, Integer code, String token) {
        this.message = message;
        this.code = code;
        this.token = token;
    }

    public loginResult(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public static loginResult successLogin(String token){
        return new loginResult("操作成功",Code.NORMAL,token);
    }
    public static loginResult fail(Integer code,String errorMessage){
        return new loginResult(errorMessage,code);
    }
}
