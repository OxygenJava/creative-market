package com.creative.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class walletDTO {
    private Integer id;
    private BigDecimal balanceAccount;
    private Integer userId;
    //钱包状态(0为正常状态，1为冻结状态)
    private Integer state;
    //钱包功能是否开启(1为开启，默认是0未开启)
    private Integer isOpen;
}
