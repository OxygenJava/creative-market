package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class crow {

    @TableId(type = IdType.AUTO)
    //项目的id
    private Integer id;
    //项目标题
    private String title;
    //项目的介绍
    private String description;
    //项目团队的id
    private Integer tid;
    //项目的未来回报
    private Double future;
    //项目的制作周期
    private Integer cycle;
    //项目的众筹资金
    private Double Crowdfunding_money;
    //项目的众筹时间
    private LocalDateTime Crowdfunding_time;
    //项目的众筹前的宣传时间
    private LocalDateTime publicize_time;
    //项目的平台分成
    private Double divide_money;
    //项目的宣传资金
    private Double publicize_money;
    //项目的标签（类别）
    private String project_classify;





}
