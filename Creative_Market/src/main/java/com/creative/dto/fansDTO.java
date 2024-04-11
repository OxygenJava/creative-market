package com.creative.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class fansDTO {
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

    //是否回关状态
    //单方关注：0
    //相互关注：1
    private Integer isConcern;
}
