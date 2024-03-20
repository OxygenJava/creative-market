package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.commodity;
import com.creative.domain.commodityHomePage;
import com.creative.domain.recommend;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.service.commodityHomePageService;
import com.creative.service.recommendService;
import com.creative.utils.beanUtil;
import com.creative.utils.userHolder;
import com.creative.utils.weightUtils;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class commodityHomePageServiceImpl implements commodityHomePageService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private commodityServiceImpl commodityService;
    @Autowired
    private recommendService recommendService;
    @Value("${creativeMarket.shopImage}")
    private String shopImage;
    @Override
    public Result getInformationToHomePage(String token) {

        JSONObject jsonObject = new JSONObject(token);
        String token1 = jsonObject.getStr("token");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(token1);

        //当token中查不到账号，直接(打乱)返回所有信息
        if (entries.isEmpty()){
            List<commodityHomePage> pageList = getCommodityHomePageList();
            Collections.shuffle(pageList);
            return Result.success(pageList);
        }

        //获取已登录的用户
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries,new UserDTO(),true);
        //获取该用户的所有权重
        List<recommend> recommendList = recommendService.lambdaQuery().eq(recommend::getUserId, userDTO.getId()).list();
        //获取所有商品
        List<commodity> commodityList = commodityService.query().list();

        //获取每一个商品的权重
        Map<commodity, Double> commodityWeight = getCommodityWeight(recommendList, commodityList);
        //根据权重获取返回出去的数据顺序
        List<commodity> returnList = getReturnList(commodityWeight);

        return Result.success(returnList);
    }




    /************************** commodityHomePageServiceImpl内部方法  *****************/

    /**
     * 查询commodity表，并拷贝该对象到commodityHomePage中
     * @return
     */
    public List<commodityHomePage> getCommodityHomePageList(){
        List<commodityHomePage> list1 = new ArrayList<>();
        List<commodity> list = commodityService.list();
        for (commodity commodity : list) {
            commodityHomePage commodityHomePage = beanUtil.copyCommodity(shopImage, commodity);
            list1.add(commodityHomePage);
        }
        return list1;
    }

    /**
     * 根据权重获取返回出去的数据
     * @param commodityWeight
     * @return
     */
    public List<commodity> getReturnList(Map<commodity, Double> commodityWeight) {
        List<commodity> returnList = new ArrayList<>();
        weightUtils weightUtils = new weightUtils(commodityWeight);
        while (!commodityWeight.isEmpty()){
            commodity draw = weightUtils.draw();
            if (draw == null){
                for (Map.Entry<commodity, Double> entry : commodityWeight.entrySet()) {
                    if (entry.getValue() == 0.0){
                        returnList.add(entry.getKey());
                        commodityWeight.remove(entry.getKey());
                        break;
                    }
                }
            }else {
                returnList.add(draw);
                commodityWeight.remove(draw);
            }
        }
        return returnList;
    }

    /**
     * 获取每一个商品的权重
     * @param recommendList
     * @param commodityList
     * @return
     */
    public Map<commodity, Double> getCommodityWeight(List<recommend> recommendList, List<commodity> commodityList) {
        //键 -> 商品id, 值 -> 每件商品权重
        Map<commodity,Double> commodityWeight = new HashMap<>();
        //遍历商品集合
        for (commodity commodity : commodityList) {
            String labelId = commodity.getLabelId();
            //记录该商品的权重总和
            double sumWeight = 0.0;
            //遍历权重集合
            for (recommend recommend : recommendList) {
                //获取每一个商品的labelId拼接后的字符串
                String[] labelIds = labelId.split(",");
                for (String s : labelIds) {
                    //表示该商品拥有与权重集合相同的标签id
                    if (recommend.getLabelId() == Integer.parseInt(s)){
                        sumWeight += recommend.getWeight();
                        break;
                    }
                }
            }
            commodityWeight.put(commodity,sumWeight);
        }
        return commodityWeight;
    }
}
