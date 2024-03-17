package com.creative.dto;

import lombok.Data;

@Data
public class resetPasswordFrom {
    private String phoneNumber;
    private String password;
    private String confirmPassword;
}
