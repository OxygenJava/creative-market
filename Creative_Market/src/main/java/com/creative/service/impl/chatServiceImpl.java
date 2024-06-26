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
import com.creative.utils.imgUtils;
import com.creative.utils.websocketgetHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Value("${creativeMarket.iconImage}")
    private String iconImage;

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

            chatUserLink chatUserLink1=new chatUserLink();

            Integer linkId = selectAssociation(user.getUsername(), toUser);
            chatUserLink chatuserLink = chatuserMapper.selectById(linkId);
            LocalDateTime nowTime = LocalDateTime.now();

            LambdaQueryWrapper<chatList> lqw1=new LambdaQueryWrapper<>();
            lqw1.eq(com.creative.domain.chatList::getFromUser,toUser);
            List<com.creative.domain.chatList> chatLists1 = chatLMapper.selectList(lqw1);




            if(chatuserLink==null){
                chatList chatList=new chatList();

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
                            else {
                                chatList.setToWindow(0);
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

                List<chatMessage> selectsocket = chatMMapper.selectsocket(user.getUsername(), toUser);
                return new Result(Code.SYNTAX_ERROR,"First",selectsocket);
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
                LambdaQueryWrapper<chatList> lqw2=new LambdaQueryWrapper<>();
                lqw2.eq(com.creative.domain.chatList::getToUser,user.getUsername())
                        .eq(com.creative.domain.chatList::getFromUser,toUser);
                chatList chatList1 = chatLMapper.selectOne(lqw2);


                if(chatList1!=null){
                        chatList1.setUnread(0);
                        chatLMapper.updateById(chatList1);
                }
                else {
                    List<chatMessage> selectsocket = chatMMapper.selectsocket(user.getUsername(), toUser);
                    return new Result(Code.SYNTAX_ERROR,toUser+"还没回复您信息",selectsocket);
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
        if(chatList!=null){
        if(chatList.getToWindow()==0){
            chatList.setUnread(chatList.getUnread()+1);
            chatLMapper.updateById(chatList);
        }
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
        if(user==null){
            return new Result(Code.SYNTAX_ERROR, "没有查询到用户", "");
        }
        else {

            ArrayList<user> list=new ArrayList();

            LambdaQueryWrapper<chatUserLink> lqw=new LambdaQueryWrapper<>();
            lqw.eq(chatUserLink::getFromUser,user.getUsername())
                    .or()
                    .eq(chatUserLink::getToUser,user.getUsername());
            List<chatUserLink> chatUserLinks = chatuserMapper.selectList(lqw);


            for (chatUserLink chatUserLink : chatUserLinks) {
                LambdaQueryWrapper<user> lqw1=new LambdaQueryWrapper<>();
                lqw1.eq(com.creative.domain.user::getUsername,chatUserLink.getToUser())
                        .or()
                        .eq(com.creative.domain.user::getUsername,chatUserLink.getFromUser());
                List<com.creative.domain.user> users = userMapper.selectList(lqw1);
                if(users.size()!=0){
                try {
                    for (com.creative.domain.user user1 : users) {
                        user1.setIconImage(imgUtils.encodeImageToBase64(iconImage + "\\" + user1.getIconImage()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
                list.addAll(users);
            }

            List<UserDTO> userDTOS = BeanUtil.copyToList(list, UserDTO.class);
            userDTOS=userDTOS.stream().filter(userDTO1 -> !userDTO1.getUsername().equals(user.getUsername()))
                    .collect(Collectors.toList());
            List<UserDTO> collect = userDTOS.stream().distinct().collect(Collectors.toList());

            Integer code = chatUserLinks!=null? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = chatUserLinks!=null? "查询成功" : "查询失败";
            return new Result(code, msg, collect);
        }

    }

    @Override
    public Result selectToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(String.valueOf(authorization));
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        if(userDTO.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        user user = userMapper.selectById(userDTO.getId());
        Integer code = user!=null? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = user!=null? "查询成功" : "查询失败";
        return new Result(code, msg, user.getUsername());

    }


}
