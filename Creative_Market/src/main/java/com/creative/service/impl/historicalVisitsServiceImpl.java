package com.creative.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.historicalVisits;
import com.creative.domain.lable;
import com.creative.mapper.historicalVisitsMapper;
import com.creative.mapper.LableMapper;
import com.creative.service.historicalVisitsService;
import com.creative.service.LableService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class historicalVisitsServiceImpl extends ServiceImpl<historicalVisitsMapper, historicalVisits>
        implements historicalVisitsService {
//
//    @Override
//    public void getHistoricalVisitsList() {
//
//    }
}
