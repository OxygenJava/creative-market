package com.creative.controller;

import com.creative.dto.Result;
import com.creative.dto.homePageDTO;
import com.creative.service.commodityHomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/homePage")
public class homePageController {

    @Autowired
    private commodityHomePageService commodityHomePageService;

    /**
     * 获取主页信息返回指前端
     * 这里应使用推荐算法
     * @return
     */
    @PostMapping()
    public Result getInformationToHomePage(@RequestBody String token) throws ExecutionException, InterruptedException, IOException {
        return commodityHomePageService.getInformationToHomePage(token);
    }

    /**
     * 分页获取主页信息并返回前端
     * @param homePageDTO
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @PostMapping("/HomePageByPage")
    public Result getInformationToHomePageByPage(@RequestBody homePageDTO homePageDTO) throws ExecutionException, InterruptedException, IOException {
       return commodityHomePageService.getInformationToHomePageByPage(homePageDTO);
    }
}
