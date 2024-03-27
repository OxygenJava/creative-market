package com.creative.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class commodityHomePage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //商品id
    private Integer commodityId;
    //主页图片宽
    private Integer width;
    //主页图片高
    private Integer height;
    //主页图片
    private String homePageImage;
    //商品状态
    private Integer state;
    //支持者数量
    private Integer supportNumber;
    //点赞者数量
    private Integer likesReceived;
    //描述
    private String description;
    //已众筹数量
    private Double crowdfundedAmount;
    //标签文字
    private String label;
}
