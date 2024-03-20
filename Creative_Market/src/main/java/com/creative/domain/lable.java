package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class lable {
    //标签id
    @TableId(type = IdType.AUTO)
    private Integer id;
    //标签名称
    private String name;
    //标签是否热门
    @TableField(value = "is_popular")
    private Integer isPopular;
    //标签的创建时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    //标签的状态
    private Integer state;
    //标签的
    private Integer visitsNumber;


}
