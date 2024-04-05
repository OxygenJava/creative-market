package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class discoveredLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer discoveredId;
    //点赞时间
    private LocalDateTime likeTime;
}
