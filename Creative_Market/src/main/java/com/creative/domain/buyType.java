package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class buyType {
    @TableId(type = IdType.AUTO)
    private Integer buyId;
    private Integer commodityId;
    private String buyType;
    private Integer buyMoney;
}
