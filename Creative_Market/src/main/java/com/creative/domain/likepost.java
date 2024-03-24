package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class likepost {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer pid;
}
