package com.creative.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class postDTO {
    //帖子id
    @TableId(type = IdType.AUTO)
    private Integer id;
    //帖子标题
    private String title;
    //帖子正文
    private String body;

    //帖子标签
    private List<String> lableId;

    //帖子发布者用户名
    private String postUserNickName;

    //帖子的发布时间
    private String createTime;

    //帖子发布的时间年月日
    private String releasedTime;

    //帖子的点赞数
    private Integer likes;
    //帖子的点赞状态
    private Integer likesState;
    //帖子的收藏数
    private Integer collection;
    //帖子的收藏状态
    private Integer collectionState;

    //帖子发布者头像
    private String iconImage;
    private Integer uid;
    //帖子图片路径
    private List<String> image;


}
