package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface postMapper extends BaseMapper<post>{
}
