package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.concern;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.concernMapper;
import com.creative.mapper.userMapper;
import com.creative.service.concernService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
public class concernServiceImpl implements concernService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private concernMapper concernMapper;

    @Autowired
    private userMapper userMapper;


    //关注
    @Override
    public Result concernPerson(concern concern, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            concern.setUid(user.getId());
            LocalDateTime dateTime = LocalDateTime.now();
            concern.setConcernTime(dateTime);
            int insert = concernMapper.insert(concern);


            Integer code = insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = insert > 0  ? "关注成功" : "关注失败";
            return new Result(code, msg, "");
        }

    }

    //取消关注


    @Override
    public Result cancelConcern(concern concern, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            LambdaQueryWrapper<concern> lqw=new LambdaQueryWrapper<>();
            lqw.eq(com.creative.domain.concern::getUid,user.getId())
                    .eq(com.creative.domain.concern::getConcernId,concern.getConcernId());
            int delete = concernMapper.delete(lqw);
            Integer code = delete > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = delete > 0  ? "取消关注成功" : "取消关注失败";
            return new Result(code, msg, "");
        }
    }

    //统计关注的人数
    @Override
    public Result countConcern(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            LambdaQueryWrapper<concern> lqw=new LambdaQueryWrapper<>();
            lqw.eq(com.creative.domain.concern::getConcernId,user.getId());
            Integer integer = concernMapper.selectCount(lqw);
            Integer code = integer > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = integer > 0  ? "统计成功" : "统计失败失败";
            return new Result(code, msg, integer);
        }
    }
}
