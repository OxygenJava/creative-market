package com.creative.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.creative.domain.orderTable;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface orderMapper extends BaseMapper<orderTable> {
}
