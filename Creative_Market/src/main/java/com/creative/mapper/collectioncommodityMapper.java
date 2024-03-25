package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.collectioncommodity;
import com.creative.domain.collectionpost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface collectioncommodityMapper extends BaseMapper<collectioncommodity> {
    @Delete("delete from collectioncommodity where uid=#{uid} and cid=#{pid}")
    int deleteBycollectioncommodity(collectioncommodity collectioncommodity);
}
