package com.creative.controller;

import com.creative.dto.Result;
import com.creative.service.commodityHomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result getInformationToHomePage(@RequestBody String token){
        return commodityHomePageService.getInformationToHomePage(token);
    }
}
