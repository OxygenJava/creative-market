package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodity;
import com.creative.dto.Result;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public interface commodityService extends IService<commodity> {
    @Transactional(rollbackFor = Exception.class)
    Result selectCommodityById(Integer id, HttpServletRequest request) throws IOException;
}
