package com.creative.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String e_mail;
    private String nickName;
    private String iconImage;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private String address;
    private Integer fansCount;
    private Integer FocusCount;
}
