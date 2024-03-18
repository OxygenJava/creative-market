package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class order {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    private Integer payState;
    private Integer orderState;
}
