package com.creative.dto;

import lombok.Data;

import java.util.List;

@Data
public class getCommentByPageDTO {
    private Integer fatherId;
    private String userNickName;
    private String userIconImage;
    //回复正文
    private String content;
    //回复帖子
    private Integer postId;
    //现在的时间和回复的时间差
    private String createTime;

    //存放子级评论的集合
    List<getCommentByPageChildDTO> getCommentByPageChildDTOList;
}
