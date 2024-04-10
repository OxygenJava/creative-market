package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class wallet {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String payPassword;

    @Min(value = 0,message = "余额不能低于0元")
    private BigDecimal balanceAccount;
    private Integer userId;
    //钱包状态(0为正常状态，1为冻结状态)
    private Integer state;
    //钱包功能是否开启(1为开启，默认是0未开启)
    private Integer isOpen;

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creatTime;

    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
