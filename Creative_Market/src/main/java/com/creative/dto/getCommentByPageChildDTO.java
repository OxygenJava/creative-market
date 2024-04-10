package com.creative.dto;

import lombok.Data;

@Data
/**
 * 分页获取评论
 * 子级评论的返回数据
 */
public class getCommentByPageChildDTO {
    private Integer id;
    private String userNickName;
    private String userIconImage;
    //回复正文
    private String content;
    //回复目标
    private String targetNickName;
    //现在的时间和回复的时间差
    private String createTime;
}
