package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.dto.commodityDTO;
import com.creative.mapper.*;
import com.creative.service.*;
import com.creative.utils.beanUtil;
import com.creative.utils.imgUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
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
    private  commodityMapper commodityMapper;

    @Autowired
    private  userMapper userMapper;

    @Autowired
    private LableMapper lableMapper;

    @Value("${creativeMarket.detailsImage}")
    private String detailsImage;

    @Autowired
    private commodityDetailsImageService commodityDetailsImageService;

    @Autowired
    private likecommodityMapper likecommodityMapper;
    @Autowired
    private collectioncommodityMapper collectioncommodityMapper;

    @Autowired
    private commodityHomePageMapper commodityHomePageMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
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
        one.setLikesState(0);
        one.setCollectionState(0);

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

        //设置点赞状态
        LambdaQueryWrapper<likecommodity> likecommodityLqw = new LambdaQueryWrapper<>();
        likecommodityLqw.eq(likecommodity::getCid,id).eq(likecommodity::getUid,userDTO.getId());
        likecommodity likecommodity = likecommodityMapper.selectOne(likecommodityLqw);
        if (likecommodity != null){
            one.setLikesState(1);
        }else {
            one.setLikesState(0);
        }

        //设置收藏状态
        LambdaQueryWrapper<collectioncommodity> collectioncommodityLqw = new LambdaQueryWrapper<>();
        collectioncommodityLqw.eq(collectioncommodity::getCid,id).eq(collectioncommodity::getUid,userDTO.getId());
        collectioncommodity collectioncommodity = collectioncommodityMapper.selectOne(collectioncommodityLqw);
        if (collectioncommodity != null){
            one.setCollectionState(1);
        }else {
            one.setCollectionState(0);
        }

        File imageFile = new File(this.shopImage,one.getHomePageImage());
        if (!imageFile.exists()){
            one.setHomePageImage(null);
        }else {
            one.setHomePageImage(imgUtils.encodeImageToBase64ByFile(imageFile));
        }
        return Result.success(one);
    }

    //发布（插入）
    @Override
    public Result insertCom(MultipartFile[] file, commodityDTO commodityDTO, HttpServletRequest request) throws IOException {
        //获取请求头信息
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        //判断用户是否登录
        if(entries.isEmpty()){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录");
        }

        //校验数据
        if (commodityDTO.getTargetCrowdfundingAmount() == null || commodityDTO.getTargetCrowdfundingAmount() == 0.0){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请填写预计众筹金额");
        }
        if (commodityDTO.getLabel() == null || "".equals(commodityDTO.getLabel())){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请填写标签");
        }
        if (commodityDTO.getDescription() == null || "".equals(commodityDTO.getDescription())){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请描述商品");
        }
        if (commodityDTO.getReleaseAddress() == null || "".equals(commodityDTO.getReleaseAddress())){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请填写发布地址");
        }
        if (commodityDTO.getTeamId() == null || "".equals(commodityDTO.getTeamId())){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请填写团队个人用户id");
        }
        if (commodityDTO.getCrowdfundingDay() == null){
            commodityDTO.setCrowdfundingDay(30);
        }

        //获取到登录(发布)的用户
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        commodity commodity = BeanUtil.copyProperties(commodityDTO, commodity.class);
        //给商品绑定用户id
        commodity.setReleaseUserId(userDTO.getId());
        //注入发布时间
        commodity.setReleaseTime(LocalDateTime.now());
        //注入预计众筹时间 = 现在时间 + 14天
        commodity.setBeginCrowdfundingTime(LocalDateTime.now().plusDays(14));
        //根据label获取到响应的labelId，如果没有，则创建
        String[] labels = commodityDTO.getLabel().split(",");
        StringBuilder labelSb  = new StringBuilder();
        for (String label : labels) {
            lable one = lableService.lambdaQuery().eq(lable::getName, label).one();
            //通过name查询到label存在，则获取id，并拼接
            if (one != null){
                labelSb.append(one.getId()+",");
            }else {
                //此时,用户插入的标签在标签库中不存在
                lable l = new lable();
                l.setName(label);
                l.setCreateTime(LocalDateTime.now());
                int i = lableMapper.insertAll(l);
                if (i < 0){
                    return Result.fail(Code.SYNTAX_ERROR,"插入标签失败");
                }
                labelSb.append(l.getId()+",");
            }
        }
        commodity.setLabelId(labelSb.toString());

        if (file == null || file.length <= 0){
            return Result.fail(Code.SYNTAX_ERROR,"上传的图片不能为空");
        }

        //处理图片(下载图片到服务器)
        for (int i = 0; i < file.length; i++) {
            String originalFilename = file[i].getOriginalFilename();

            //获取图片后缀
            String imageLastName = originalFilename.substring(originalFilename.lastIndexOf("."));
            //校验图片的格式
            if (!imageLastName.equals(".jpg") && !imageLastName.equals(".png")){
                return Result.fail(Code.SYNTAX_ERROR,"图片格式必须为 jgp 或 png 格式");
            }
            File baseFile = new File(detailsImage);
            File homePageImageFile = new File(shopImage);
            if (!baseFile.exists()){
                baseFile.mkdirs();
            }
            if (!homePageImageFile.exists()){
                homePageImageFile.mkdirs();
            }

            String imageName = UUID.randomUUID().toString();
            //选用第一张上传的图片当首页图片
            if (i == 0){
                //下载图片
                file[i].transferTo(new File(homePageImageFile,imageName+imageLastName));
                //设置商品的首页图片地址
                commodity.setHomePageImage(imageName+imageLastName);
                commodityHomePage commodityHomePage = beanUtil.copyCommodity(shopImage, commodity);
                commodityHomePage.setLabel(commodityDTO.getLabel().replace(",",""));
                //保存商品到数据库
                save(commodity);
                //设置刚保存的商品的id到首页表中
                commodityHomePage.setCommodityId(commodity.getId());
                //设置状态
                commodityHomePage.setState(0);
                commodityHomePageMapper.insert(commodityHomePage);

                //将commodityHomePage对象放进消息队列，待监听器处理
                System.out.println("待存进es的commodityHomePage已纳入消息队列：id="+commodityHomePage.getId());
                commodityHomePage.setHomePageImage(imgUtils.encodeImageToBase64(shopImage+"//"+commodityHomePage.getHomePageImage()));
                amqpTemplate.convertAndSend("topic_exchange","searchRouting", JSON.toJSONString(commodityHomePage));
            }else {
                file[i].transferTo(new File(baseFile,imageName+imageLastName));
                commodityDetailsImage commodityDetailsImage = new commodityDetailsImage();
                commodityDetailsImage.setCommodityId(commodity.getId());
                commodityDetailsImage.setImage(imageName+imageLastName);
                commodityDetailsImageService.save(commodityDetailsImage);
            }
        }
        return Result.success(commodity.getId());
    }

    @Override
    public Result deleteCom(Integer id) {
        int delete = commodityMapper.deleteById(id);
        Integer code = delete > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete > 0 ? "删除成功" : "删除失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result updateCom(commodity commodity) {

            int insert = commodityMapper.updateById(commodity);
            Integer code = insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = insert > 0 ? "修改成功" : "修改失败";
            return new Result(code, msg, "");

    }

    @Override
    public Result selectComAll() {
        List<commodity> commodities = commodityMapper.selectList(null);
        Integer code = commodities != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = commodities != null ? "查询成功" : "查询失败";
        return new Result(code, msg, commodities);
    }

    @Override
    public Result selectComLable(Integer id) {
        commodity commodity = commodityMapper.selectById(id);
        ArrayList<Integer> list=new ArrayList<>();
        String[] split = commodity.getLabelId().split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.parseInt(split[i]));
        }
        List<lable> lables = lableMapper.selectBatchIds(list);
        Integer code = lables != null && commodity!=null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = lables != null && commodity!=null ? "查询成功" : "查询失败";
        return new Result(code, msg, lables);
    }

    @Override
    public Result selectComTeam(Integer id) {
        commodity commodity = commodityMapper.selectById(id);
        ArrayList<Integer> list=new ArrayList<>();
        String[] split = commodity.getTeamId().split(",");
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.parseInt(split[i]));
        }
        List<user> users = userMapper.selectBatchIds(list);
        Integer code = users != null && commodity!=null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = users != null && commodity!=null ? "查询成功" : "查询失败";
        return new Result(code, msg, users);
    }

    //根据用户id查询该用户发布过的商品
    @Override
    public Result selectByUidAllCommodity(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        //判断用户是否登录
        if(entries.isEmpty()){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录");
        }
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        LambdaQueryWrapper<commodity> lqw=new LambdaQueryWrapper<>();


        lqw.eq(commodity::getReleaseUserId,userDTO.getId());
        List<commodity> commodities = commodityMapper.selectList(lqw);
        for (commodity commodity : commodities) {
            LambdaQueryWrapper<likecommodity> lqw1=new LambdaQueryWrapper<>();
            LambdaQueryWrapper<collectioncommodity> lqw2=new LambdaQueryWrapper<>();

            try {
                commodity.setHomePageImage(imgUtils.encodeImageToBase64(shopImage+"\\"+commodity.getHomePageImage()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            lqw1.eq(likecommodity::getUid, userDTO.getId())
                    .eq(likecommodity::getCid,commodity.getId());
            likecommodity likecommodity = likecommodityMapper.selectOne(lqw1);
            if(likecommodity!=null){
                commodity.setLikesState(1);
            }
            else {
                commodity.setLikesState(0);
            }

            lqw2.eq(collectioncommodity::getUid,userDTO.getId())
                    .eq(collectioncommodity::getCid,commodity.getId());
            collectioncommodity collectioncommodity = collectioncommodityMapper.selectOne(lqw2);
            if(collectioncommodity!=null){
                commodity.setCollectionState(1);
            }
            else {
                commodity.setCollectionState(0);
            }

        }

        Integer code = commodities != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = commodities != null ? "查询成功" : "查询失败";
        return new Result(code, msg, commodities);
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
