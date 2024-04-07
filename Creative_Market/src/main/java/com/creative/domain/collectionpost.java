package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class collectionpost {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer pid;
    private LocalDateTime createTime;
}
