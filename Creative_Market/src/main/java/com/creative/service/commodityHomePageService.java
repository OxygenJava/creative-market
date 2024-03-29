package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodityHomePage;
import com.creative.dto.Result;
import com.creative.dto.homePageDTO;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface commodityHomePageService extends IService<commodityHomePage>{

    Result getInformationToHomePage(String token) throws InterruptedException, ExecutionException, IOException;

    /**
     * 按照权重获取商品并进行分页分页
     * @param homePageDTO
     * pageSize     每页显示的数据条数
     * PageNumber   要显示的页数
     * @return
     */
    Result getInformationToHomePageByPage(homePageDTO homePageDTO) throws InterruptedException, ExecutionException, IOException;
}
