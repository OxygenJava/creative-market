package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class chatList {
    @TableId(type = IdType.AUTO)
    private Integer listId;
    private Integer linkId;
    private String fromUser;
    private String toUser;
    //发送方是否在窗口
    private Integer fromWindow;
    //接收方是否在窗口
    private Integer toWindow;
    //未读数
    private Integer unread;
    //列表状态，是否删除
    private Integer status;

}
