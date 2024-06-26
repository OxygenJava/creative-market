package com.creative.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class getAllDiscoverDTO{
    private Integer id;
    private String userName;
    private String title;
    private String body;
    private Integer collection;
    private Integer likes;
    //帖子标签
    private String label;
    //发布时间距离现在的时间值
    private String releasedTime;
    //用户头像图片
    private String iconImage;
    private String[] image;

    //帖子的点赞状态
    private Integer likesState;

    //帖子的收藏状态
    private Integer collectionState;
}
