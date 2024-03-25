package com.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.commodity;
import com.creative.domain.likecommodity;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.likecommodityMapper;
import com.creative.service.likecommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class likecommodityServicImpl implements likecommodityService {

    @Autowired
    private commodityMapper commodityMapper;

    @Autowired
    private likecommodityMapper likecommodityMapper;


    @Override
    public Result ClickLikecommodity(likecommodity likecommodity) {
        commodity commodity = commodityMapper.selectById(likecommodity.getCid());
        commodity.setLikesReceived(commodity.getLikesReceived()+1);
        commodity.setLikesState(1);
        int update = commodityMapper.updateById(commodity);
        int insert = likecommodityMapper.insert(likecommodity);
        Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && insert>0? "点赞成功" : "点赞失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result CancelLikecommodity(likecommodity likecommodity) {

            commodity commodity = commodityMapper.selectById(likecommodity.getCid());
            commodity.setLikesReceived(commodity.getLikesReceived()-1);
            commodity.setLikesState(0);
            int update = commodityMapper.updateById(commodity);
            int insert = likecommodityMapper.deleteBylikecommodity(likecommodity);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "取消点赞成功" : "取消点赞失败";
            return new Result(code, msg, "");

    }

    @Override
    public Result selectLikecommodity(Integer id) {
        LambdaQueryWrapper<likecommodity> lqw=new LambdaQueryWrapper<>();
        lqw.eq(likecommodity::getUid,id);
        List<likecommodity> likecommodities = likecommodityMapper.selectList(lqw);
        ArrayList<Integer> list1=new ArrayList<>();
        ArrayList<Integer> list2=new ArrayList<>();
        ArrayList<commodity> list=new ArrayList<>();

        if(likecommodities==null){
            List<commodity> commodities1 = commodityMapper.selectList(null);
            for (int i = 0; i < commodities1.size(); i++) {
                commodities1.get(i).setLikesState(0);
            }
            Integer code = commodities1 !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = commodities1 !=null? "查询成功" : "查询失败";
            return new Result(code, msg, commodities1);
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

            List<commodity> commodities3=commodityMapper.selectList(null);
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
                    commodities4.get(i).setLikesState(0);
                }
            }

            list.addAll(commodities4);
            list.addAll(commodities2);

        }
        Integer code = list !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = list !=null? "查询成功" : "查询失败";
        return new Result(code, msg, list);
    }
}
