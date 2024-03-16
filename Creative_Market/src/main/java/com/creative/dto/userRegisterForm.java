package com.creative.dto;

import lombok.Data;


@Data
public class userRegisterForm {
    private String username;
    private String password;
    private String e_mail;
    private String nickName;
    private String phoneNumber;
}
