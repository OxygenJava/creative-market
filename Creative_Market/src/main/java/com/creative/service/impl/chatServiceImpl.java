package com.creative.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.chatList;
import com.creative.domain.chatMessage;
import com.creative.domain.chatUserLink;
import com.creative.mapper.chatListMapper;
import com.creative.mapper.chatMessageMapper;
import com.creative.mapper.chatUserLinkMapper;
import com.creative.service.chatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class chatServiceImpl implements chatService {

    @Autowired
    private chatMessageMapper chatMMapper;

    @Autowired
    private chatUserLinkMapper chatuserMapper;

    @Autowired
    private chatListMapper chatLMapper;

    @Override
    public Integer selectAssociation(String fromUser, String toUser) {
      LambdaQueryWrapper<chatUserLink> lqw=new LambdaQueryWrapper<>();
      lqw.eq(chatUserLink::getFromUser,fromUser)
              .eq(chatUserLink::getToUser,toUser);
        chatUserLink chatuserLink = chatuserMapper.selectOne(lqw);
        if(chatuserLink==null){
            return null;
        }
        else {
            return chatuserLink.getLinkId();
        }

    }

    @Override
    public void isFirstChat(String fromUser, String toUser) {

        chatList chatList=new chatList();
        chatUserLink chatUserLink1=new chatUserLink();

        Integer linkId = selectAssociation(fromUser, toUser);
        chatUserLink chatuserLink = chatuserMapper.selectById(linkId);
        LocalDateTime nowTime = LocalDateTime.now();

        if(chatuserLink==null){
            chatUserLink1.setFromUser(fromUser);
            chatUserLink1.setToUser(toUser);
            chatUserLink1.setCreateTime(nowTime);
            chatuserMapper.insert(chatUserLink1);

            Integer linkId1 = selectAssociation(chatUserLink1.getFromUser(), chatUserLink1.getToUser());
            chatUserLink chatuserLink2 = chatuserMapper.selectById(linkId1);

            if(chatuserLink2!=null){
                chatList.setLinkId(chatuserLink2.getLinkId());
                chatList.setFromUser(fromUser);
                chatList.setToUser(toUser);
                chatList.setFromWindow(0);
                chatList.setToWindow(0);
                chatList.setStatus(0);
                chatList.setUnread(0);
                chatLMapper.insert(chatList);
            }
        }
        else {
            updateWindows(fromUser,toUser);

        }
    }

    @Override
    public void saveMessage(chatMessage chatMessage) {
        LocalDateTime nowTime=LocalDateTime.now();
        chatMessage.setIsLatest(0);
        chatMessage.setSendTime(nowTime);
        chatMMapper.insert(chatMessage);
    }

    @Override
    public void updateWindows(String fromUser, String toUser) {
        Integer linkId = selectAssociation(fromUser, toUser);
        LambdaQueryWrapper<chatList> lqw=new LambdaQueryWrapper<>();
        lqw.eq(chatList::getLinkId,linkId);
        List<chatList> chatLists = chatLMapper.selectList(lqw);
        for (chatList chatList : chatLists) {
            chatList.setFromWindow(1);
            chatLMapper.updateById(chatList);
        }




    }

    @Override
    public void resetWindows(String username) {
        LambdaQueryWrapper<chatList> lqw=new LambdaQueryWrapper<>();
        lqw.eq(chatList::getFromUser,username);
        List<chatList> chatLists = chatLMapper.selectList(lqw);
        for (chatList chatList : chatLists) {
            chatList.setFromWindow(0);
            chatLMapper.updateById(chatList);
        }
    }
}
