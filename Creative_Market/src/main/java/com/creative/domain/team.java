package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class team {

    @TableId(type = IdType.AUTO)
    //团队的id
    private Integer id;
    //以下都是团队成员的id，最多为15人
    private Integer uid1;
    private Integer uid2;
    private Integer uid3;
    private Integer uid4;
    private Integer uid5;
    private Integer uid6;
    private Integer uid7;
    private Integer uid8;
    private Integer uid9;
    private Integer uid10;
    private Integer uid11;
    private Integer uid12;
    private Integer uid13;
    private Integer uid14;
    private Integer uid15;


}