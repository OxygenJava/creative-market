package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class addressInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    //收货人
    private String addressee;
    //收货人号码
    private String addresseePhone;
    //收货人地址
    private String address;
    //是否为默认地址（0为否，1为是）
    private Integer state;
}
