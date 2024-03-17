package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class user {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String e_mail;
    private String phoneNumber;
    private String nickName;
    private String iconImage;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private String address;
    private LocalDateTime updateTime;
    private Integer state;
}
