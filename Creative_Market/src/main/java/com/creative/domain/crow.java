package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private Double crowMoney;
    //项目的众筹时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime crowTime;
    //项目的众筹前的宣传时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publicizeTime;
    //项目的平台分成
    private Double divideMoney;
    //项目的宣传资金
    private Double publicizeMoney;
    //图片的路径
    private String image;
    //标签1
    private String lable1;
    //标签2
    private String lable2;
    //标签3
    private String lable3;
    //标签4
    private String lable4;
    //标签5
    private String lable5;



}
