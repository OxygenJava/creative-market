package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class commodityHomePage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer commodityId;
    private Integer imageWidth;
    private Integer imageHeight;

    @TableField(exist = false)
    private String homePageImage;
    @TableField(exist = false)
    private Integer state;
    @TableField(exist = false)
    private Integer supportNumber;
    @TableField(exist = false)
    private Integer likesReceived;
    @TableField(exist = false)
    private Double crowdfundedAmount;
}
