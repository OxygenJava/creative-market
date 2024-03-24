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
import com.creative.dto.homePageDTO;
import com.creative.mapper.commodityHomePageMapper;
import com.creative.service.commodityHomePageService;
import com.creative.service.recommendService;

import com.creative.utils.weightUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class commodityHomePageServiceImpl extends ServiceImpl<commodityHomePageMapper,commodityHomePage> implements commodityHomePageService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private commodityServiceImpl commodityService;
    @Autowired
    private recommendService recommendService;


    @Value("${creativeMarket.shopImage}")
    private String shopImage;

    /**
     * 按照权重获取所有商品
     * @param token
     * @return
     */
    @Override
    public Result getInformationToHomePage(String token) {

        Map<Object, Object> entries = getUserMap(token);

        //当token中查不到账号，直接(打乱)返回所有信息
        if (entries.isEmpty()){
            List<commodityHomePage> pageList = query().list();
            Collections.shuffle(pageList);
            return Result.success(pageList);
        }

        List<commodityHomePage> returnCommodityHomePage = getReturnCommodityHomePageList(entries);
        return Result.success(returnCommodityHomePage);
    }



    /**
     * 按照权重获取商品并进行分页分页
     * @param homePageDTO
     * pageSize     每页显示的数据条数
     * PageNumber   要显示的页数
     * @return
     */
    @Override
    public Result getInformationToHomePageByPage(homePageDTO homePageDTO) {
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(homePageDTO.getToken());
        //判断缓存中用户是否存在(用户是否登录)
        if (userMap.isEmpty()){
            List<commodityHomePage> pageList = query().list();
            Collections.shuffle(pageList);
            return Result.success(pageList);
        }
        //用户已经登录，并按照权重获取到商品列表
        List<commodityHomePage> returnCommodityHomePage = getReturnCommodityHomePageList(userMap);

        //按照前端传递的分页进行集合数据切割并返回出去
        Integer pageNumber = homePageDTO.getPageNumber();
        Integer pageSize = homePageDTO.getPageSize();

        //当前面跳过的元素+要访问的元素数量 大于 商品的集合长度
            //拼接
            while (returnCommodityHomePage.size() < (pageNumber - 1) * pageSize + pageSize){
                List<commodityHomePage> CommodityHomePage = getReturnCommodityHomePageList(userMap);
                returnCommodityHomePage.addAll(CommodityHomePage);
            }

        //分页操作
        List<commodityHomePage> pageInformation = getPageInformation(returnCommodityHomePage,pageSize,pageNumber);


        System.out.println("total: " + pageInformation.size());
        return Result.success(pageInformation);
    }


    /************************** commodityHomePageServiceImpl内部方法  *****************/


    /**
     * 根据权重获取返回出去的数据
     * @param commodityWeight
     * @return
     */
    public List<commodityHomePage> getReturnList(Map<commodityHomePage, Double> commodityWeight) {
        List<commodityHomePage> returnList = new ArrayList<>();
        weightUtils weightUtils = new weightUtils(commodityWeight);
        while (!commodityWeight.isEmpty()){
            commodityHomePage draw = weightUtils.draw();
            if (draw == null){
                for (Map.Entry<commodityHomePage, Double> entry : commodityWeight.entrySet()) {
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
    public Map<commodityHomePage, Double> getCommodityWeight(List<recommend> recommendList, List<commodity> commodityList) {
        //键 -> 商品id, 值 -> 每件商品权重
        Map<commodityHomePage,Double> commodityWeight = new HashMap<>();
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
            commodityHomePage homePage = lambdaQuery().eq(commodityHomePage::getCommodityId, commodity.getId()).one();
            commodityWeight.put(homePage,sumWeight);
        }
        return commodityWeight;
    }

    /**
     * 分页
     * @param homePageList
     * @param pageSize  每页显示的数据条数
     * @param pageNumber  要显示的页数
     * @return
     */
    public List<commodityHomePage> getPageInformation(List<commodityHomePage> homePageList,int pageSize,int pageNumber){
        return homePageList.stream()
                .skip((long) (pageNumber - 1) * pageSize) // 跳过前面的数据
                .limit(pageSize) // 限制获取的数据条数
                .collect(Collectors.toList());
    }

    /**
     * 根据每个商品对应的权重给用户推送商品
     * @param entries  存放商品于对应的权重的map集合
     * @return
     */
    public List<commodityHomePage> getReturnCommodityHomePageList(Map<Object, Object> entries) {
        //获取已登录的用户
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries,new UserDTO(),true);
        //获取该用户的所有权重
        List<recommend> recommendList = recommendService.lambdaQuery().eq(recommend::getUserId, userDTO.getId()).list();
        //获取所有商品
        List<commodity> commodityList = commodityService.query().list();

        //获取每一个商品的权重
        Map<commodityHomePage, Double> commodityWeight = getCommodityWeight(recommendList, commodityList);
        //根据权重获取返回出去的数据顺序
        List<commodityHomePage> returnList = getReturnList(commodityWeight);
        return returnList;
    }

    /**
     * 根据传回来的token从缓存中获取用户集合
     * @param token
     * @return
     */
    private Map<Object, Object> getUserMap(String token) {
        JSONObject jsonObject = new JSONObject(token);
        String token1 = jsonObject.getStr("token");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(token1);
        return entries;
    }
}
