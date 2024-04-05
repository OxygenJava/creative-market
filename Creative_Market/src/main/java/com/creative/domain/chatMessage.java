package com.creative.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class chatMessage {
    @TableId(type = IdType.AUTO)
    private Integer messageId;
    private Integer linkId;
    private String fromUser;
    private String toUser;
    //消息的内容
    private String content;
    //消息的发送时间
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;
    //是否是最后一条消息
    private Integer isLatest;

}
