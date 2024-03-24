package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.likepost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface likepostMapper extends BaseMapper<likepost> {
    @Delete("delete from likepost where uid=#{uid} and pid=#{pid}")
    int deleteBylikepost(likepost likepost);


}
