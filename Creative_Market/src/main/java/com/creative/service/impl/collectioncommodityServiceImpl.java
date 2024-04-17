package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.collectioncommodityMapper;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.likecommodityMapper;
import com.creative.mapper.userMapper;
import com.creative.service.collectioncommodityService;
import com.creative.service.collectionpostService;
import com.creative.utils.imgUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @Value("${creativeMarket.shopImage}")
    private String shopImage;
    @Autowired
    private userMapper userMapper;

    @Value("${creativeMarket.iconImage}")
    private String iconImage;




    @Override
    public Result ClickCollectioncommodity(collectioncommodity collectioncommodity, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
            commodity commodity = commodityMapper.selectById(collectioncommodity.getCid());
        if(commodity==null){
            return Result.fail(Code.SYNTAX_ERROR, "商品不存在");
        }
            commodity.setCollection(commodity.getCollection()+1);
            commodity.setCollectionState(1);

        LambdaQueryWrapper<collectioncommodity> lqw = new LambdaQueryWrapper<>();
       lqw.eq(com.creative.domain.collectioncommodity::getUid,user.getId())
               .eq(com.creative.domain.collectioncommodity::getCid,collectioncommodity.getCid());
        com.creative.domain.collectioncommodity collectioncommodity1 = collectioncommodityMapper.selectOne(lqw);
        if (collectioncommodity1 != null) {
            return Result.fail(Code.SYNTAX_ERROR, "该商品您已经收藏过了");
        }

            collectioncommodity.setUid(user.getId());
            int update = commodityMapper.updateById(commodity);
            int insert = collectioncommodityMapper.insert(collectioncommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "收藏成功" : "收藏失败";
            return new Result(code, msg, "");



    }

    @Override
    public Result CancelCollectioncommodity(collectioncommodity collectioncommodity, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);


        if(user.getId()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
            commodity commodity = commodityMapper.selectById(collectioncommodity.getCid());
        if(commodity==null){
            return Result.fail(Code.SYNTAX_ERROR, "商品不存在");
        }
            commodity.setCollection(commodity.getCollection()-1);
            commodity.setCollectionState(0);
        LambdaQueryWrapper<collectioncommodity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(com.creative.domain.collectioncommodity::getUid,user.getId())
                .eq(com.creative.domain.collectioncommodity::getCid,collectioncommodity.getCid());
        com.creative.domain.collectioncommodity collectioncommodity1 = collectioncommodityMapper.selectOne(lqw);
        if (collectioncommodity1 == null) {
            return Result.fail(Code.SYNTAX_ERROR, "该商品您还没有收藏,无法取消");
        }
            collectioncommodity.setUid(user.getId());
            int update = commodityMapper.updateById(commodity);
            int insert = collectioncommodityMapper.deleteBycollectioncommodity(collectioncommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "取消收藏成功" : "取消收藏失败";
            return new Result(code, msg, "");


    }


    @Override
    public Result selectCollectioncommodity(HttpServletRequest request)  {
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
            ArrayList<collectionCommodityUser> list=new ArrayList<>();

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
                        try {
                            commodities2.get(i).setHomePageImage(imgUtils.encodeImageToBase64
                                    (shopImage+"\\"+commodities2.get(i).getHomePageImage()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                List<collectionCommodityUser> collectionCommodityUsers = BeanUtil.copyToList(commodities2, collectionCommodityUser.class);
                list.addAll(collectionCommodityUsers);
                for (collectionCommodityUser collectionCommodityUser : list) {
                    com.creative.domain.user user1 = userMapper.selectById(collectionCommodityUser.getReleaseUserId());
                    collectionCommodityUser.setNickName(user1.getNickName());
                    try {
                        collectionCommodityUser.setIconImage(imgUtils.encodeImageToBase64(iconImage + "\\" + user1.getIconImage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
            Integer code = list !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = list !=null? "查询成功" : "查询失败";
            return new Result(code, msg, list);
        }
    }
}
