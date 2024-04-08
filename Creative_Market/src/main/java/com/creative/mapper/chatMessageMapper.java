package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.chatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.websocket.server.PathParam;
import java.util.List;

@Mapper
public interface chatMessageMapper extends BaseMapper<chatMessage> {
    @Select("SELECT * FROM chat_message WHERE (from_user=#{fromUser} AND\n" +
            "to_user=#{toUser}) OR (to_user=#{fromUser} AND\n" +
            "from_user=#{toUser}) ORDER BY send_time DESC LIMIT 8")
    List<chatMessage> selectsocket(@Param("fromUser") String fromUser, @Param("toUser") String toUser);
}
