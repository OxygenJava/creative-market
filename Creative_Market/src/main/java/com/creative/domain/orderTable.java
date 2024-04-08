package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class orderTable {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    //支付状态(0为未支付，1为支付)
    private Integer payState;
    //订单状态(0为未发货，1为发货)
    private Integer orderState;
    private Integer userId;

    //收货信息类
    @TableField(exist = false)
    private addressInfo addressInfo;

    private Integer commodityId;

    //商品类
    @TableField(exist = false)
    private commodity commodity;

    private Integer addresseeId;

    //订单编号
    private String orderCode;

    //订单创建时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderTime;

    //支付类型
    private Integer payType;

    //支付时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    //支付金额
    private Integer payMoney;

    //购买类型id
    private Integer buyTypeId;
    //购买类型
    @TableField(exist = false)
    private buyType buyType;
}
