package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class commodity{

    @TableId(type = IdType.AUTO)
    private Integer id;
    //发布者id
    private Integer releaseUserId;
    //商品或赞数
    private Integer likesReceived;
    //已众筹金额
    private Double crowdfundedAmount;
    //目标众筹金额
    private Double targetCrowdfundingAmount;
    //商品首页图片
    private String homePageImage;
    //商品标签
    private String labelId;
    //商品描述
    private String description;
    //商品状态
    private Integer state;
    //支持者数量
    private Integer supportNumber;
    //商品发布时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime releaseTime;
    //商品发布地址
    private String releaseAddress;
    //开启众筹后的天数

    @TableField(exist = false)
    private Integer crowdfundingDay;
    //开启众筹时间
    @TableField(exist = false)
    private LocalDateTime beginCrowdfundingTime;
    //结束众筹时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishCrowdfundingTime;
    //下架时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime offShelfTime;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    //修改时间
    private LocalDateTime updateTime;
    //团队成员的id
    private String teamId;
    //点赞状态
    @TableField(exist = false)
    private Integer likesState;
    //收藏数
    private Integer collection;
    //收藏状态
    @TableField(exist = false)
    private Integer collectionState;

}
