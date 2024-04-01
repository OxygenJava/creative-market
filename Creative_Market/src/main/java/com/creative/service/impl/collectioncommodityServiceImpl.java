package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.collectioncommodityMapper;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.likecommodityMapper;
import com.creative.service.collectioncommodityService;
import com.creative.service.collectionpostService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Transactional
public class collectioncommodityServiceImpl implements collectioncommodityService {


    @Autowired
    private commodityMapper commodityMapper;

    @Autowired
    private collectioncommodityMapper collectioncommodityMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;




    @Override
    public Result ClickCollectioncommodity(collectioncommodity collectioncommodity, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        collectioncommodity.setUid(user.getId());


        if(collectioncommodity.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            commodity commodity = commodityMapper.selectById(collectioncommodity.getCid());
            commodity.setCollection(commodity.getCollection()+1);
            commodity.setCollectionState(1);
            int update = commodityMapper.updateById(commodity);
            int insert = collectioncommodityMapper.insert(collectioncommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "收藏成功" : "收藏失败";
            return new Result(code, msg, "");
        }


    }

    @Override
    public Result CancelCollectioncommodity(collectioncommodity collectioncommodity, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        collectioncommodity.setUid(user.getId());

        if(collectioncommodity.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            commodity commodity = commodityMapper.selectById(collectioncommodity.getCid());
            commodity.setCollection(commodity.getCollection()-1);
            commodity.setCollectionState(0);
            int update = commodityMapper.updateById(commodity);
            int insert = collectioncommodityMapper.deleteBycollectioncommodity(collectioncommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "取消收藏成功" : "取消收藏失败";
            return new Result(code, msg, "");
        }

    }

    @Override
    public Result selectAllcommodity(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }

        else {
            LambdaQueryWrapper<collectioncommodity> lqw=new LambdaQueryWrapper<>();
            lqw.eq(collectioncommodity::getUid,user.getId());
            List<collectioncommodity> collectioncommodities = collectioncommodityMapper.selectList(lqw);
            ArrayList<Integer> list1=new ArrayList<>();
            ArrayList<Integer> list2=new ArrayList<>();
            ArrayList<commodity> list=new ArrayList<>();

            if(collectioncommodities==null){
                List<commodity> commodities1 = commodityMapper.selectList(null);
                for (int i = 0; i < commodities1.size(); i++) {
                    commodities1.get(i).setCollectionState(0);
                }
                Integer code = commodities1 !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
                String msg = commodities1 !=null? "查询成功" : "查询失败";
                return new Result(code, msg, commodities1);
            }else {
                for (int i = 0; i < collectioncommodities.size(); i++) {
                    list1.add(collectioncommodities.get(i).getCid());
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
                        commodities2.get(i).setCollectionState(1);
                    }
                }

                List<commodity> commodities3 = commodityMapper.selectList(null);
                for (int i = 0; i < commodities3.size(); i++) {
                    list2.add(commodities3.get(i).getId());
                }

                list2.removeAll(list1);


                List<commodity> commodities4=new ArrayList<>();
                for (int i = 0; i < list2.size(); i++) {
                    LambdaQueryWrapper<commodity> lqw2=new LambdaQueryWrapper<>();
                    lqw2.eq(commodity::getId,list2.get(i));
                    commodity commodity = commodityMapper.selectOne(lqw2);
                    commodities4.add(commodity);
                }
                if(commodities4!=null){
                    for (int i = 0; i < commodities4.size(); i++) {
                        commodities4.get(i).setCollectionState(0);
                    }
                }

                list.addAll(commodities2);
                list.addAll(commodities4);
                Collections.shuffle(list);

            }
            Integer code = list !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = list !=null? "查询成功" : "查询失败";
            return new Result(code, msg, list);
        }

    }

    @Override
    public Result selectCollectioncommodity(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }

        else {
            LambdaQueryWrapper<collectioncommodity> lqw=new LambdaQueryWrapper<>();
            lqw.eq(collectioncommodity::getUid,user.getId());
            List<collectioncommodity> collectioncommodities = collectioncommodityMapper.selectList(lqw);
            ArrayList<Integer> list1=new ArrayList<>();
            ArrayList<commodity> list=new ArrayList<>();

            if(collectioncommodities==null){
                return new Result(Code.SYNTAX_ERROR, "", "");
            }else {
                for (int i = 0; i < collectioncommodities.size(); i++) {
                    list1.add(collectioncommodities.get(i).getCid());
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
                        commodities2.get(i).setCollectionState(1);
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
