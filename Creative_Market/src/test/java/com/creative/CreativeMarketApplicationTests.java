package com.creative;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.creative.domain.*;
import com.creative.service.*;
import com.creative.service.impl.userServiceImpl;
import com.creative.utils.RegexUtils;
import com.creative.utils.beanUtil;
import com.creative.utils.imgUtils;
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
    @Test
    void lableServiceTest(@Autowired lableService lableService){
        lable lable = new lable();
        lable.setName("13代酷睿");
        lable.setCreateTime(LocalDateTime.now());
        lableService.save(lable);
    }

    @Test
    void getImageWidth(@Autowired commodityService service, @Autowired commodityHomePageService com) throws IOException {
        List<commodity> list = service.query().list();
        for (commodity commodity : list) {
            commodityHomePage commodityHomePage = beanUtil.copyCommodity(this.shopImage,commodity);
        }
    }

    @Test
    void weightTest(@Autowired recommendService recommendService){
        List<recommend> list = recommendService.query().list();
        System.out.println(list);
    }

    @Test
    void historicalVisitsTest(@Autowired historicalVisitsService historicalVisitsService){
//        historicalVisitsService.getHistoricalVisitsList();
    }
}
