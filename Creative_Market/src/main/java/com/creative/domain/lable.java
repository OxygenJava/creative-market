package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class lable {
    @TableId(type = IdType.AUTO)
    private String id;
    private String name;
    private Integer isPopular;
    private LocalDateTime createTime;
    private Integer state;
}
