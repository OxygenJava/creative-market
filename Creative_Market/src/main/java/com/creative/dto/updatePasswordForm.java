package com.creative.dto;

import lombok.Data;


@Data
public class updatePasswordForm {
    private String originalPassword;
    private String newPassword;
    private String confirmNewPassword;
}
