package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

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
    private String lable;
    //帖子用户id
    private Integer uid;

}
