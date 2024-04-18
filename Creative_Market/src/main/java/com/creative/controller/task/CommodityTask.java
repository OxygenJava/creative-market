package com.creative.controller.task;

import com.creative.domain.commodity;
import com.creative.domain.commodityHomePage;
import com.creative.service.commodityHomePageService;
import com.creative.service.commodityService;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Controller
public class CommodityTask {

    @Autowired
    private commodityService commodityService;
    @Autowired
    private commodityHomePageService commodityHomePageService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Scheduled(fixedRate = 1000) // 每秒钟检查一次
    public void checkExpiration() throws IOException {
        List<commodity> list = commodityService.lambdaQuery().eq(commodity::getState,0).list();
        for (commodity commodity : list) {
            LocalDateTime releaseTime = commodity.getReleaseTime();
            //查询现在时间是否在预计众筹时间之后
            if (LocalDateTime.now().isAfter(releaseTime.plusDays(14))){
                if (commodity.getBeginCrowdfundingTime() == null){
                    LocalDateTime localDateTime = releaseTime.plusDays(14);
                    commodity.setBeginCrowdfundingTime(localDateTime);
                }

                //更新首页
                commodityHomePage one = commodityHomePageService.lambdaQuery().
                        eq(commodityHomePage::getCommodityId, commodity.getId()).one();
                one.setState(1);
                commodity.setState(1);

//                //修改搜索引擎
                UpdateRequest request = new UpdateRequest("app_seacher", String.valueOf(one.getId()))
                        .doc("state", 1);
                restHighLevelClient.update(request, RequestOptions.DEFAULT);

                commodityHomePageService.updateById(one);
                commodityService.updateById(commodity);
            }
        }
    }
}
