package com.creative.dto;

import lombok.Data;

@Data
public class commodityDTO {
    //发布者id
    private Integer releaseUserId;
    //目标众筹金额
    private Double targetCrowdfundingAmount;
    //商品标签
    private String label;
    //商品描述
    private String description;
    //商品发布地址
    private String releaseAddress;
    //团队成员的id
    private String teamId;
    //开启众筹后的天数
    private Integer crowdfundingDay;
}
