package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.addressInfo;
import com.creative.domain.commodity;
import com.creative.domain.pay;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.payDTO;
import com.creative.mapper.addressInfoMapper;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.payMapper;
import com.creative.service.payService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class payServiceImpl implements payService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private payMapper payMapper;

    @Autowired
    private addressInfoMapper addressInfoMapper;

    @Autowired
    private commodityMapper commodityMapper;

    /**
     * 添加支付信息
     *
     * @param pay
     * @param request
     * @return
     */
    @Override
    public Result payAdd(pay pay, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() != null) {
            pay.setUserId(user.getId());
            pay.setPayTime(LocalDateTime.now());
            int insert = payMapper.insert(pay);
            boolean flag = insert > 0;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "添加成功" : "添加失败");
        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }

    @Override
    public Result paySelect(Integer commodityId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        payDTO payDTO = new payDTO();
        if (user.getId() != null) {
            LambdaQueryWrapper<addressInfo> lqw = new LambdaQueryWrapper<>();
            lqw.eq(addressInfo::getState, 1);
            addressInfo addressInfo = addressInfoMapper.selectOne(lqw);
            payDTO.setAddressInfo(addressInfo);
            commodity commodity = commodityMapper.selectById(commodityId);
            payDTO.setCommodity(commodity);
            boolean flag = addressInfo != null && commodity != null;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功": "查询失败", payDTO);
        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }
}
