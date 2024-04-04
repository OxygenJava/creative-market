package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.chatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface chatMessageMapper extends BaseMapper<chatMessage> {
}
