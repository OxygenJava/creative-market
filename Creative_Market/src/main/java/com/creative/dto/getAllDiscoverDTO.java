package com.creative.dto;

import lombok.Data;

@Data
public class getAllDiscoverDTO{
    private Integer id;
    private String userName;
    private String title;
    private String content;
    private Integer favoritesNumber;
    private Integer likesNumber;
    //发布时间距离现在的时间值
    private String releasedTime;
    //用户头像图片
    private String iconImage;
    private String[] image;
}
