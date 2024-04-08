package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.addressInfo;
import com.creative.domain.buyType;
import com.creative.domain.commodity;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.payDTO;
import com.creative.mapper.addressInfoMapper;
import com.creative.mapper.buyTypeMapper;
import com.creative.mapper.commodityMapper;
import com.creative.service.payService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class payServiceImpl implements payService {
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private addressInfoMapper addressInfoMapper;

    @Autowired
    private commodityMapper commodityMapper;

    @Autowired
    private buyTypeMapper buyTypeMapper;


    /**
     * 支付界面查询
     * @param commodityId   商品id
     * @param buyTypeId     购买类型id
     * @param request
     * @return
     */
    @Override
    public Result paySelect(Integer commodityId,Integer buyTypeId, HttpServletRequest request) {
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
            buyType buyType = buyTypeMapper.selectById(buyTypeId);
            payDTO.setBuyType(buyType);
            boolean flag = addressInfo != null && commodity != null && buyType != null;
            return new Result(flag ? Code.NORMAL : Code.SYNTAX_ERROR, flag ? "查询成功": "查询失败", payDTO);
        } else {
            return new Result(Code.SYNTAX_ERROR, "请先登录");
        }
    }
}
