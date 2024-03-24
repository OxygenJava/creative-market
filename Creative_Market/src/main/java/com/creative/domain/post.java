package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class post {
    //帖子id
    @TableId(type = IdType.AUTO)
    private Integer id;
    //帖子标题
    private String title;
    //帖子正文
    private String body;
    //帖子图片路径
    private String image;
    //帖子标签
    private String lableId;
    //帖子用户id
    private Integer uid;
    //帖子的发布时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    //帖子的点赞数
    private Integer likes;
    //帖子的点赞状态
    @TableField(exist = false)
    private Integer likesState;
    //帖子的收藏数
    private Integer collection;
    //帖子的收藏状态
    @TableField(exist = false)
    private Integer collectionState;

}
