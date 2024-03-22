package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.mapper.commodityMapper;
import com.creative.mapper.userMapper;
import com.creative.service.LableService;
import com.creative.service.commodityService;
import com.creative.service.historicalVisitsService;
import com.creative.service.recommendService;
import com.creative.utils.imgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class commodityServiceImpl extends ServiceImpl<commodityMapper, commodity> implements commodityService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LableService lableService;
    @Autowired
    private historicalVisitsService historicalVisitsService;
    @Autowired
    private recommendService recommendService;
    @Value("${creativeMarket.shopImage}")
    private String shopImage;

    @Autowired
    private userMapper userMapper;


    /**
     * 用户点击某个商品，跳转到商品详情页
     * @param id
     * @return
     */
    @Override
    public Result selectCommodityById(Integer id, HttpServletRequest request) throws IOException {
        //获取请求头信息
        String authorization = request.getHeader("Authorization");
        //携带的token中查询用户信息
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);

        //如果查询到的为空，则返回用户未登录的信息
        if (entries.isEmpty()){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"尚未登录");
        }

        //获取到登录的用户信息
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);

        //查询商品详情信息
        commodity one = lambdaQuery().eq(commodity::getId, id).one();

        if (one == null){
            return Result.fail(Code.SYNTAX_ERROR,"商品不存在");
        }

        //获取labelId并进行切割，得到商品的标签数组
        String[] labelIds = one.getLabelId().split(",");
        //每次访问，标签的浏览次数+1(处理label表)
        //往权重表添加数据(处理权重表)
        handleLabelVisitTimeAndWeight(labelIds,userDTO);
        //添加用户历史记录(处理historical_visits表)
        handleHistoricalVisits(userDTO, one);
        one.setHomePageImage(imgUtils.encodeImageToBase64(this.shopImage+"\\"+one.getHomePageImage()));

        return Result.success(one);
    }



    /*************************  commodityServiceImpl内部方法   ******************************/

    /**
     * 每次访问，标签的浏览次数+1
     * 往权重表添加数据
     * @param labelIds
     */
    public void handleLabelVisitTimeAndWeight(String[] labelIds,UserDTO userDTO) {
        for (String labelId : labelIds) {
            updateLabelVisitTime(labelId);
            handleWeight(labelId,userDTO);
        }
    }

    /**
     * 添加用户历史记录(处理historical_visits表)
      * @param userDTO
     * @param one
     */
    public void handleHistoricalVisits(UserDTO userDTO, commodity one) {

        historicalVisits historicalOne = historicalVisitsService.lambdaQuery()
                .eq(historicalVisits::getVisitUserId, userDTO.getId())
                .eq(historicalVisits::getVisitCommodityId, one.getId()).one();
        //使用用户id＋商品id查询有没有相同的历史记录，如果没有，插入一条新的历史记录
        if (historicalOne == null){
            historicalVisits hv = new historicalVisits();
            hv.setVisitUserId(userDTO.getId());
            hv.setVisitCommodityId(one.getId());
            hv.setCreateTime(LocalDateTime.now());
            hv.setVisitTime(1);
            historicalVisitsService.save(hv);
        }else {
            //如果这条历史记录已存在，表示用户曾经访问过
            //将访问次数加1
            historicalOne.setVisitTime(historicalOne.getVisitTime()+1);
            //添加修改时间
            historicalOne.setUpdateTime(LocalDateTime.now());
            historicalVisitsService.updateById(historicalOne);
        }
    }

    /**
     * 每次访问，标签的浏览次数+1
     * @param labelId
     */
    public void updateLabelVisitTime(String labelId){
        //labelId表插入浏览次数
        lable label = lableService.lambdaQuery().eq(lable::getId, labelId).one();
        //设置label浏览为：原来的次数+1
        label.setVisitsNumber(label.getVisitsNumber()+1);
        //保存到数据库
        lableService.updateById(label);
    }

    /**
     * 往权重表添加数据
     * @param labelId
     * @param userDTO
     */
    public void handleWeight(String labelId,UserDTO userDTO){
        //查询user_id和label_id的权重是否已经存在
        recommend recommendOne = recommendService.lambdaQuery()
                .eq(recommend::getLabelId, labelId)
                .eq(recommend::getUserId, userDTO.getId()).one();

        //不存在，创建新的权重
        if (recommendOne == null){
            recommend rec = new recommend();
            rec.setLabelId(Integer.parseInt(labelId));
            rec.setUserId(userDTO.getId());
            rec.setWeight(0.1);
            recommendService.save(rec);
        }else {
            //权重已经存在，将原有的权重+0.1
            recommendOne.setWeight(recommendOne.getWeight()+0.1);
            recommendService.updateById(recommendOne);
        }
    }
}
