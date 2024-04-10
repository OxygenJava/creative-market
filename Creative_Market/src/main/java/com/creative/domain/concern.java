package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class concern {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //用户id
    private Integer uid;
    //被关注的用户id
    private Integer concernId;
    //被关注的时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime concernTime;

}
