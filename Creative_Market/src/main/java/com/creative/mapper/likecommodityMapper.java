package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.collectioncommodity;
import com.creative.domain.likecommodity;
import com.creative.domain.likepost;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface likecommodityMapper extends BaseMapper<likecommodity> {
    @Delete("delete from likecommodity where uid=#{uid} and cid=#{cid}")
    int deleteBylikecommodity(likecommodity likecommodity);
}
