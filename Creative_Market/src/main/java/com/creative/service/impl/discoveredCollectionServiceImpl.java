package com.creative.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.discoveredCollection;
import com.creative.dto.Result;
import com.creative.mapper.discoveredCollectionMapper;
import com.creative.service.discoveredCollectionService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class discoveredCollectionServiceImpl extends ServiceImpl<discoveredCollectionMapper, discoveredCollection> implements discoveredCollectionService {

}
