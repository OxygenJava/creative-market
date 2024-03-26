package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.collectionpost;
import com.creative.domain.likepost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface collectionpostMapper extends BaseMapper<collectionpost> {
    @Delete("delete from collectionpost where uid=#{uid} and pid=#{pid}")
    int deleteBycollpost(collectionpost collectionpost);
}
