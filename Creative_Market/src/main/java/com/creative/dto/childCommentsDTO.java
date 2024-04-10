package com.creative.dto;

import lombok.Data;

@Data
/**
 * 添加子级评论
 * 从前端接收数据
 */
public class childCommentsDTO {
    private Integer fatherCommentsId;
    private Integer postId;
    private String content;
    //回复目标(用户id)
    private Integer target;
}
