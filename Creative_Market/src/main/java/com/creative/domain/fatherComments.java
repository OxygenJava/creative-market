package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class fatherComments {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //回复用户
    private Integer userId;
    //回复正文
    private String content;
    //回复帖子
    private Integer postId;
    //回复时间
    private Long createTime;
}
