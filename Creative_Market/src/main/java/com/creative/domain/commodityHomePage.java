package com.creative.domain;


import lombok.Data;

@Data
public class commodityHomePage {
    private Integer id;
    private Integer commodityId;
    private Integer imageWidth;
    private Integer imageHeight;

    private String homePageImage;
    private Integer state;
    private Integer supportNumber;
    private Integer likesReceived;
    private Double crowdfundedAmount;
}
