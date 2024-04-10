package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class childComments {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //父级评论的id
    private Integer fatherCommentsId;
    //回复用户
    private Integer userId;
    //回复正文
    private String content;
    //回复目标
    private Integer target;
    //回复时间
    private Long createTime;
}
