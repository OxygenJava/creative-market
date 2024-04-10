package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.commodity;
import com.creative.domain.likecommodity;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.likecommodityMapper;
import com.creative.service.likecommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class likecommodityServicImpl implements likecommodityService {

    @Autowired
    private commodityMapper commodityMapper;

    @Autowired
    private likecommodityMapper likecommodityMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;




    @Override
    public Result ClickLikecommodity(likecommodity likecommodity,HttpServletRequest request) {
                String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        likecommodity.setUid(user.getId());


        if(likecommodity.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            commodity commodity = commodityMapper.selectById(likecommodity.getCid());
            commodity.setLikesReceived(commodity.getLikesReceived()+1);
            commodity.setLikesState(1);
            int update = commodityMapper.updateById(commodity);
            int insert = likecommodityMapper.insert(likecommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "点赞成功" : "点赞失败";
            return new Result(code, msg, "");
        }

    }

    @Override
    public Result CancelLikecommodity(likecommodity likecommodity,HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        likecommodity.setUid(user.getId());


        if(likecommodity.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            commodity commodity = commodityMapper.selectById(likecommodity.getCid());
            commodity.setLikesReceived(commodity.getLikesReceived()-1);
            commodity.setLikesState(0);
            int update = commodityMapper.updateById(commodity);
            int insert = likecommodityMapper.deleteBylikecommodity(likecommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "取消点赞成功" : "取消点赞失败";
            return new Result(code, msg, "");
        }


    }
    

    @Override
    public Result selectLikecommodity(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            LambdaQueryWrapper<likecommodity> lqw=new LambdaQueryWrapper<>();
            lqw.eq(likecommodity::getUid,user.getId());
            List<likecommodity> likecommodities = likecommodityMapper.selectList(lqw);
            ArrayList<Integer> list1=new ArrayList<>();
            ArrayList<commodity> list=new ArrayList<>();

            if(likecommodities==null){
                return new Result( Code.SYNTAX_ERROR, "", "");
            }
            else {
                for (int i = 0; i < likecommodities.size(); i++) {
                    list1.add(likecommodities.get(i).getCid());
                }

                List<commodity> commodities2=new ArrayList<>();
                for (int i = 0; i < list1.size(); i++) {
                    LambdaQueryWrapper<commodity> lqw1=new LambdaQueryWrapper<>();
                    lqw1.eq(commodity::getId,list1.get(i));
                    commodity commodity = commodityMapper.selectOne(lqw1);
                    commodities2.add(commodity);
                }

                if(commodities2!=null){
                    for (int i = 0; i < commodities2.size(); i++) {
                        commodities2.get(i).setLikesState(1);
                    }
                }

                list.addAll(commodities2);

            }

            Integer code = list !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = list !=null? "查询成功" : "查询失败";
            return new Result(code, msg, list);
        }
    }


    }

