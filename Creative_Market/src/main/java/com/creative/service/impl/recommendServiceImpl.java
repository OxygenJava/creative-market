package com.creative.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.recommend;
import com.creative.mapper.recommendMapper;
import com.creative.service.recommendService;
import org.springframework.stereotype.Service;

@Service
public class recommendServiceImpl extends ServiceImpl<recommendMapper, recommend> implements recommendService {
}
