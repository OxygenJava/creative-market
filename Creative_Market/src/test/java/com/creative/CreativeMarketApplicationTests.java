package com.creative;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.creative.domain.*;
import com.creative.mapper.LableMapper;
import com.creative.service.*;
import com.creative.domain.user;
import com.creative.service.impl.userServiceImpl;
import com.creative.utils.RegexUtils;
import com.creative.utils.beanUtil;
import com.creative.utils.imgUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class CreativeMarketApplicationTests {

    @Autowired
    private userServiceImpl userService;

    @Value("${creativeMarket.shopImage}")
    private String shopImage;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        user admin = userService.lambdaQuery().eq(user::getUsername, "admin").one();
        admin.setPassword(DigestUtil.md5Hex(admin.getPassword()));
        userService.updateById(admin);
        System.out.println(admin);
    }

    @Test
    void regexTest(){
        boolean b = RegexUtils.phoneMatches("13542152222");
        System.out.println(b);
    }

    @Test
    void redis(){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("login:code","code",1, TimeUnit.SECONDS);
    }

    @Test
    void passwordTest(){
//        String s = DigestUtil.md5Hex("25345656");
//        String s1 = null;
//        String s2 = DigestUtil.md5Hex(s1);
//
////        System.out.println(s1.equals(s));
//        System.out.println(s.equals(s2));
        String s = RandomUtil.randomString(15);
        System.out.println("user_"+s);
    }

    @Test
    void imageTest(){
        String s = "adc.jpg";
        String substring = s.substring(s.lastIndexOf("."));
        System.out.println(substring);
    }

    @Test
    void commodityServiceTest(@Autowired commodityService service){
        commodity c = new commodity();
        c.setReleaseUserId(1);
        c.setTargetCrowdfundingAmount(3999.00);
        c.setHomePageImage("cde68dfd235d3978.jpg");
        c.setLabelId("11,12,13,14");
        c.setDescription("电脑 荣耀笔记本X14 2024 13代酷睿i5-13500H 16G 1T 100%sRGB高色域 长续航 14吋护眼全面屏轻薄笔记本电脑");
        c.setReleaseTime(LocalDateTime.now());
        c.setReleaseAddress("广东-广州-天河区");
        service.save(c);
    }
    @Autowired
    private LableMapper lableMapper;
//    @Test
//    void lableServiceTest(@Autowired LableService lableService){
//        lable lable = new lable();
//        lable.setName("13");
//        lable.setCreateTime(LocalDateTime.now());
//        int i = lableMapper.insertAll(lable);
//        System.out.println(lable.getId());
//    }
//
//    @Autowired
//    private LableService lableService;
//    @Test
//    void getImageWidth(@Autowired commodityService service, @Autowired commodityHomePageService com) throws IOException {
//        List<commodity> list = service.query().list();
//
//        int index = 1;
//        for (commodity commodity : list) {
//            String labelId = commodity.getLabelId();
//            String[] split = labelId.split(",");
//            String label = "";
//            for (String s : split) {
//                lable one = lableService.lambdaQuery().eq(lable::getId, s).one();
//                label += one.getName();
//            }
//            commodityHomePage commodityHomePage = beanUtil.copyCommodity(shopImage, commodity);
//            commodityHomePage.setLabel(label);
//            commodityHomePage.setId(index);
//            com.updateById(commodityHomePage);
//            index++;
//        }
//    }

    @Test
    void weightTest(@Autowired recommendService recommendService){
        List<recommend> list = recommendService.query().list();
        System.out.println(list);
    }

    @Test
    void historicalVisitsTest(@Autowired historicalVisitsService historicalVisitsService){
//        historicalVisitsService.getHistoricalVisitsList();
    }

    /**
     * es初始化，批量导入数据
     */
    @Test
    void esInit(@Autowired commodityHomePageService homePageService, @Autowired RestHighLevelClient restHighLevelClient) throws IOException {
        List<commodityHomePage> list = homePageService.query().list();
        System.out.println(list.size());
        for (commodityHomePage commodityHomePage : list) {
            commodityHomePage.setHomePageImage(imgUtils.encodeImageToBase64(shopImage+"//"+commodityHomePage.getHomePageImage()));
        }
        BulkRequest bulk = new BulkRequest("app_seacher");
        for (commodityHomePage commodityHomePage : list) {
            IndexRequest indexRequest = new IndexRequest().id(commodityHomePage.getId().toString())
                    .source(JSON.toJSONString(commodityHomePage), XContentType.JSON);
            //批量添加
            bulk.add(indexRequest);
        }
        restHighLevelClient.bulk(bulk, RequestOptions.DEFAULT);
    }

    @Test
     void StringTest(){
        String s = "盯盯拍行车记录仪MINI3S升级版 3K高清影像 超大存储拓展 AI驾驶辅助";
        String s1 = "行车记录仪";
        char[] chars = s1.toCharArray();
        int i = s.indexOf(s1);
        System.out.println(i);
    }

    @Test
    void showEs(@Autowired RestHighLevelClient restHighLevelClient) throws IOException {
        GetRequest getRequest = new GetRequest("app_seacher","16");
        GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = documentFields.getSourceAsString();
        System.out.println(sourceAsString);
    }
}
