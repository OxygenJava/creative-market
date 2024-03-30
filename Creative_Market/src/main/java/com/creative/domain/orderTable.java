package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class orderTable {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    private Integer payState;
    private Integer orderState;
    private Integer payId;
    private Integer userId;
    @TableField(exist = false)
    private addressInfo addressInfo;

    private Integer commodityId;
    @TableField(exist = false)
    private commodity commodity;

    private Integer addresseeId;
}
