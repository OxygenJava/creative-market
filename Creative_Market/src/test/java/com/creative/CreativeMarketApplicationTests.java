package com.creative;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.creative.domain.user;
import com.creative.service.impl.userServiceImpl;
import com.creative.utils.RegexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class CreativeMarketApplicationTests {

    @Autowired
    private userServiceImpl userService;

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
}
