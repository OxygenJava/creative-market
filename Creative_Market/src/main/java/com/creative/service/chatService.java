package com.creative.service;

import com.creative.domain.chatMessage;

public interface chatService {
    //查询聊天双方的关联id
    Integer selectAssociation( String fromUser, String toUser);
    void isFirstChat(String fromUser, String toUser);
    //保存聊天记录
    void saveMessage(chatMessage chatMessage);
    //更新是否在同一窗口值
    void updateWindows(String fromUser, String toUser);
    //重置窗口值
    void resetWindows(String username);

}
