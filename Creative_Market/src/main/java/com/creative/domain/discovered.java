package com.creative.domain;

import lombok.Data;

@Data
public class discovered {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String image;
    private Integer favoritesNumber;
    private Integer likesNumber;
    private long releasedTime;
}
