package com.creative.controller.task;

import com.creative.domain.commodity;
import com.creative.service.commodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Controller
public class CommodityTask {

    @Autowired
    private commodityService commodityService;

    @Scheduled(fixedRate = 1000) // 每秒钟检查一次
    public void checkExpiration(){
//        System.out.println(666);
        List<commodity> list = commodityService.lambdaQuery().eq(commodity::getState, 0).list();
        for (commodity commodity : list) {
            LocalDateTime releaseTime = commodity.getReleaseTime();
            //查询现在时间是否在预计众筹时间之后
            if (LocalDateTime.now().isAfter(releaseTime.plusDays(14))){
                if (commodity.getBeginCrowdfundingTime() == null){
                    LocalDateTime localDateTime = releaseTime.plusDays(14);
                    commodity.setBeginCrowdfundingTime(localDateTime);
                }
                commodity.setState(1);
                commodityService.updateById(commodity);
            }
        }
    }
}
