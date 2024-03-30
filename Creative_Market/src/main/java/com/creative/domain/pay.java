package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class pay {
    @TableId(type = IdType.AUTO)
    private Integer payID;
    private Integer payMoney;
    private Integer payType;
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    private Integer userId;
    private Integer commodityId;
}
