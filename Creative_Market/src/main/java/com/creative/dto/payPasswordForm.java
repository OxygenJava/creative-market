package com.creative.dto;

import lombok.Data;

@Data
public class payPasswordForm {
    //原密码(支付密码)
    private String originalPassword;
    //新密码
    private String newPassword;
    //确认新密码(确认支付密码)
    private String confirmNewPassword;
}
