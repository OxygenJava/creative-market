package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class pay {
    @TableId(type = IdType.AUTO)
    private Integer payID;
    private Integer payMoney;
    private String payType;
    private LocalDateTime payTime;
}
