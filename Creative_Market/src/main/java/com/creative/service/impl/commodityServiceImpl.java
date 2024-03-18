package com.creative.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.commodity;
import com.creative.mapper.commodityMapper;
import com.creative.service.commodityService;
import org.springframework.stereotype.Service;

@Service
public class commodityServiceImpl extends ServiceImpl<commodityMapper, commodity> implements commodityService {
}
