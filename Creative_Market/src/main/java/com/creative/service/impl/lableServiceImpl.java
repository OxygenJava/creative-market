package com.creative.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.lable;
import com.creative.mapper.lableMapper;
import com.creative.service.lableService;
import org.springframework.stereotype.Service;

@Service
public class lableServiceImpl extends ServiceImpl<lableMapper, lable> implements lableService {
}
