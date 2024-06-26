package com.creative.service;

import com.creative.domain.chatMessage;
import com.creative.dto.Result;
import org.w3c.dom.html.HTMLTableCaptionElement;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

public interface chatService {
    //查询聊天双方的关联id
    Integer selectAssociation( String fromUser, String toUser);
    Result isFirstChat(HttpServletRequest request, String toUser);
    //保存聊天记录
    void saveMessage(chatMessage chatMessage);
    //更新是否在同一窗口值(fromUser)
    void updateWindows(String fromUser, String toUser);
    //重置窗口值
    void resetWindows(String username);

    void addUnread(Integer linkId);

    void updateend(String fromUser,String toUser);

    //获取聊天用户的信息
    Result selectChatUser(HttpServletRequest request);

    //获取token的username
    Result selectToken(HttpServletRequest request);








}
