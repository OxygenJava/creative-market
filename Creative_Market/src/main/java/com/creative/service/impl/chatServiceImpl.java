package com.creative.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.chatList;
import com.creative.domain.chatMessage;
import com.creative.domain.chatUserLink;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.mapper.chatListMapper;
import com.creative.mapper.chatMessageMapper;
import com.creative.mapper.chatUserLinkMapper;
import com.creative.mapper.userMapper;
import com.creative.service.chatService;
import com.creative.utils.websocketgetHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class chatServiceImpl implements chatService {

    @Autowired
    private chatMessageMapper chatMMapper;

    @Autowired
    private com.creative.mapper.userMapper userMapper;

    @Autowired
    private chatUserLinkMapper chatuserMapper;

    @Autowired
    private chatListMapper chatLMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
    public Result isFirstChat(HttpServletRequest request, String toUser) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(String.valueOf(authorization));
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        user user = userMapper.selectById(userDTO.getId());
        if(userDTO.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }else if(user.getUsername().equals(toUser)){
            return new Result(Code.SYNTAX_ERROR,"请不要给自己发消息","");
        }
        else {
            chatList chatList=new chatList();
            chatUserLink chatUserLink1=new chatUserLink();

            Integer linkId = selectAssociation(user.getUsername(), toUser);
            chatUserLink chatuserLink = chatuserMapper.selectById(linkId);
            LocalDateTime nowTime = LocalDateTime.now();

            LambdaQueryWrapper<chatList> lqw1=new LambdaQueryWrapper<>();
            lqw1.eq(com.creative.domain.chatList::getFromUser,toUser);
            List<com.creative.domain.chatList> chatLists1 = chatLMapper.selectList(lqw1);

            if(chatLists1.size()!=0 ){
                for (com.creative.domain.chatList list : chatLists1) {
                    if(list.getFromWindow()==1){
                    list.setUnread(0);
                    chatLMapper.updateById(list);
                    break;
                    }
                }

            }


            if(chatuserLink==null){
                chatUserLink1.setFromUser(user.getUsername());
                chatUserLink1.setToUser(toUser);
                chatUserLink1.setCreateTime(nowTime);
                chatuserMapper.insert(chatUserLink1);

                Integer linkId1 = selectAssociation(chatUserLink1.getFromUser(), chatUserLink1.getToUser());
                chatUserLink chatuserLink2 = chatuserMapper.selectById(linkId1);

                if(chatuserLink2!=null){

                    if(chatLists1.size()!=0 ){
                        for (com.creative.domain.chatList list : chatLists1) {
                            if(list.getFromWindow()==1){
                                chatList.setToWindow(1);
                            }

                    }
                    }
                    else {
                        chatList.setToWindow(0);
                    }
                    chatList.setLinkId(chatuserLink2.getLinkId());
                    chatList.setFromUser(user.getUsername());
                    chatList.setToUser(toUser);
                    chatList.setFromWindow(1);
                    chatList.setStatus(0);
                    chatList.setUnread(0);
                    chatLMapper.insert(chatList);


                }

                LambdaQueryWrapper<chatList> lqw=new LambdaQueryWrapper<>();
                lqw.eq(com.creative.domain.chatList::getToUser,user.getUsername());
                List<com.creative.domain.chatList> chatLists = chatLMapper.selectList(lqw);
                if(chatLists.size()!=0) {
                    for (com.creative.domain.chatList list : chatLists) {
                        list.setToWindow(1);
                        chatLMapper.updateById(list);
                    }
                }

                return new Result(Code.SYNTAX_ERROR,"First","");
            }

            else {
                updateWindows(user.getUsername(),toUser);
                LambdaQueryWrapper<chatList> lqw=new LambdaQueryWrapper<>();
                lqw.eq(com.creative.domain.chatList::getToUser,user.getUsername());
                List<com.creative.domain.chatList> chatLists = chatLMapper.selectList(lqw);
                if(chatLists.size()!=0) {
                    for (com.creative.domain.chatList list : chatLists) {
                        list.setToWindow(1);
                        chatLMapper.updateById(list);
                    }
                }
                List<chatMessage> selectsocket = chatMMapper.selectsocket(user.getUsername(), toUser);
                Collections.reverse(selectsocket);

                return new Result(Code.SYNTAX_ERROR,"No First",selectsocket);
            }
        }

    }

    @Override
    public void saveMessage(chatMessage chatMessage) {
        LocalDateTime nowTime=LocalDateTime.now();
        chatMessage.setIsLatest(1);
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

        LambdaQueryWrapper<chatList> lqw1=new LambdaQueryWrapper<>();
        lqw1.eq(chatList::getToUser,username);
        List<chatList> chatLists1 = chatLMapper.selectList(lqw1);
        for (chatList chatList : chatLists1) {
            chatList.setToWindow(0);
            chatLMapper.updateById(chatList);
        }
    }

    @Override
    public void addUnread(Integer linkId) {
        LambdaQueryWrapper<chatList> lqw=new LambdaQueryWrapper<>();
        lqw.eq(chatList::getLinkId,linkId);
        chatList chatList = chatLMapper.selectOne(lqw);
        if(chatList.getToWindow()==0){
            chatList.setUnread(chatList.getUnread()+1);
            chatLMapper.updateById(chatList);
        }
    }

    @Override
    public void updateend(String fromUser,String toUser) {

        LambdaQueryWrapper<chatMessage> lqw=new LambdaQueryWrapper<>();
        lqw.eq(chatMessage::getFromUser,fromUser)
                .eq(chatMessage::getToUser,toUser);
        List<chatMessage> chatMessages = chatMMapper.selectList(lqw);
        for (chatMessage chatMessage : chatMessages) {
            chatMessage.setIsLatest(0);
            chatMMapper.updateById(chatMessage);
        }
    }

    @Override
    public Result selectChatUser(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(String.valueOf(authorization));
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);

        if(userDTO.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }

        user user = userMapper.selectById(userDTO.getId());
        ArrayList<user> list=new ArrayList();

        LambdaQueryWrapper<chatUserLink> lqw=new LambdaQueryWrapper<>();
        lqw.eq(chatUserLink::getFromUser,user.getUsername());
        List<chatUserLink> chatUserLinks = chatuserMapper.selectList(lqw);


        for (chatUserLink chatUserLink : chatUserLinks) {
            LambdaQueryWrapper<user> lqw1=new LambdaQueryWrapper<>();
            lqw1.eq(com.creative.domain.user::getUsername,chatUserLink.getToUser());
            com.creative.domain.user user1 = userMapper.selectOne(lqw1);
            list.add(user1);
        }

        List<UserDTO> userDTOS = BeanUtil.copyToList(list, UserDTO.class);

        Integer code = chatUserLinks!=null? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = chatUserLinks!=null? "查询成功" : "查询失败";
        return new Result(code, msg, userDTOS);

    }


}
