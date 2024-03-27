package com.creative.dto;

import lombok.Data;

@Data
public class userSearchDTO {
    private String searchInfo;
    private Integer pageSize;
    private Integer pageNumber;
}
