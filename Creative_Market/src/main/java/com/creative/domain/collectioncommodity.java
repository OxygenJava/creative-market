package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class collectioncommodity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer cid;
}
