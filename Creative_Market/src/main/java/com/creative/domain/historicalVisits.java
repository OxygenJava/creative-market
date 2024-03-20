package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class historicalVisits {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer visitUserId;
    private Integer visitCommodityId;
    private Integer visitTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic(value = "0",delval = "1")
    private Integer version;
}
