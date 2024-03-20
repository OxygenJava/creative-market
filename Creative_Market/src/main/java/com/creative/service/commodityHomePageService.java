package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodityHomePage;
import com.creative.dto.Result;

public interface commodityHomePageService{

    Result getInformationToHomePage(String token);
}
