package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.discovered;
import com.creative.domain.discoveredLike;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.dto.getAllDiscoverDTO;
import com.creative.mapper.DiscoveredMapper;
import com.creative.service.discoverService;
import com.creative.service.discoveredLikeService;
import com.creative.service.userService;
import com.creative.utils.imgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class discoverServiceImpl extends ServiceImpl<DiscoveredMapper, discovered> implements discoverService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private userService userService;
    @Value("${creativeMarket.discoverImage}")
    private String discoverImage;
    @Value("${creativeMarket.iconImage}")
    private String iconImage;
    @Autowired
    private discoveredLikeService discoveredLikeService;
    /**
     * 发布论坛
     * @param file
     * @param disc
     * @param request
     * @return
     */
    @Override
    public Result uploadDiscover(MultipartFile[] file, discovered disc, HttpServletRequest request) throws IOException {
        //登录校验
        UserDTO userDTO = getUserDTO(request);
        if (userDTO == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }

        if (file == null || file.length <= 0){
            return Result.fail(Code.SYNTAX_ERROR,"上传的图片不能为空");
        }
        //设置用户id
        disc.setUserId(userDTO.getId());
        //设置发布时间
        disc.setReleasedTime(new Date().getTime());
        //用于记录图片字符串，使用逗号拼接
        String image = "";
        //处理图片
        for (MultipartFile multipartFile : file) {
            String originalFilename = multipartFile.getOriginalFilename();
            //获取图片后缀
            String imageLastName = originalFilename.substring(originalFilename.lastIndexOf("."));
            //校验图片的格式
            if (!imageLastName.equals(".jpg") && !imageLastName.equals(".png")){
                return Result.fail(Code.SYNTAX_ERROR,"图片格式必须为 jgp 或 png 格式");
            }
            File discoverImageFile = new File(discoverImage);
            System.out.println(discoverImageFile);
            if (!discoverImageFile.exists()){
                discoverImageFile.mkdirs();
            }

            //生成图片名字
            String imageName = UUID.randomUUID().toString();
            //拼接图片字符串
            image += imageName+imageLastName+",";
            //下载图片
            multipartFile.transferTo(new File(discoverImageFile,imageName+imageLastName));
        }
        //将图片字符串设置在对象中
        disc.setImage(image);
        //将该对象存库
        save(disc);
        System.out.println(disc);
        return Result.success();
    }

    /**
     * 分页获取发现中内容
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @Override
    public Result getAllDiscover(int pageSize, int pageNumber) throws IOException {
        Page<discovered> discoveredPage = new Page<>(pageNumber,pageSize);
        Page<discovered> page = page(discoveredPage);
        List<discovered> records = page.getRecords();
        if (records.size() <= 0){
            return Result.success("数据已经到底啦~");
        }
        List<getAllDiscoverDTO> list = new ArrayList<>();
        for (discovered record : records) {
            getAllDiscoverDTO getAllDiscoverDTO = BeanUtil.copyProperties(record, getAllDiscoverDTO.class);
            //根据用户id查询用户
            user one = userService.lambdaQuery().eq(user::getId, record.getUserId()).one();
            getAllDiscoverDTO.setUserName(one.getNickName());

            //识别发布时间
            Long releasedTime = record.getReleasedTime();
            //获取当前时间值
            long nowTime = new Date().getTime();
            String timeStr = "";
            //当前时间 - 发布时间 = 相差时间
            long l = nowTime - releasedTime;
            //以分钟为单位
            long time = l / 1000 / 60;
            //当计算出来的时间值小于1时
            if (time == 0){
                timeStr = 1+"分钟前";
            }else if (time / 60 > 0){
                //当相差的时间/60大于0，表示可以使用小时为单位
                timeStr = time / 60+"小时前";
            }else if (time / 60 / 24 > 0){
                //当相差的时间/60 / 24大于0，表示可以使用天为单位
                timeStr = time / 60 / 24 + "天前";
            }else {
                timeStr = time + "分钟前";
            }
            getAllDiscoverDTO.setReleasedTime(timeStr);
            //设置用户头像
            if (one.getIconImage() != null){
                String s = imgUtils.encodeImageToBase64(iconImage);
                getAllDiscoverDTO.setIconImage(s);
            }
            //处理图片
            String[] image = getAllDiscoverDTO.getImage();
            List<String> imageList = new ArrayList<>();
            for (String s : image) {
                if (!"".equals(s)){
                    String s1 = imgUtils.encodeImageToBase64(discoverImage + "\\" + s);
                    imageList.add(s1);
                }
            }
            String[] arrayStr = new String[imageList.size()];
            getAllDiscoverDTO.setImage(imageList.toArray(arrayStr));
            list.add(getAllDiscoverDTO);
        }
        return Result.success(list);
    }

    /**
     * 用户点赞
     * @param request
     * @param discoveredId
     * @return
     */
    @Override
    public Result discoveredLike(HttpServletRequest request, Integer discoveredId) {
        //登录校验
        UserDTO userDTO = getUserDTO(request);
        if (userDTO == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }

        //通过id获取发现信息
        discovered one = getDiscovered(discoveredId);
        if (one == null){
            return Result.fail(Code.SYNTAX_ERROR,"该论坛不存在");
        }
        //判断用户是否已经对该论坛点赞
        discoveredLike discoveredLike1 = discoveredLikeService.lambdaQuery().
                eq(discoveredLike::getUserId, userDTO.getId()).
                eq(discoveredLike::getDiscoveredId, discoveredId).one();
        if (discoveredLike1 != null){
            return Result.fail(Code.SYNTAX_ERROR,"您已经对该论坛点过赞了！！");
        }
        //将该论坛的点赞数取出并+1
        one.setLikesNumber(one.getLikesNumber() + 1);
        //处理了点赞数之后修改论坛表
        updateById(one);
        //并插入论坛点赞表
        discoveredLike discoveredLike = new discoveredLike();
        discoveredLike.setUserId(userDTO.getId());
        discoveredLike.setDiscoveredId(discoveredId);
        discoveredLike.setLikeTime(LocalDateTime.now());
        discoveredLikeService.save(discoveredLike);
        /**
         * 给用户发送信息代码
         */
        return Result.success();
    }

    /**
     * 用户取消点赞
     *
     * @param request
     * @param discoveredId
     * @return
     */
    @Override
    public Result cancelDiscoveredLike(HttpServletRequest request, Integer discoveredId) {
        //登录校验
        UserDTO userDTO = getUserDTO(request);
        if (userDTO == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        discovered one = getDiscovered(discoveredId);
        if (one == null){
            return Result.fail(Code.SYNTAX_ERROR,"该论坛不存在");
        }
        //判断用户是否已经对该论坛点赞
        discoveredLike discoveredLike1 = discoveredLikeService.lambdaQuery().
                eq(discoveredLike::getUserId, userDTO.getId()).
                eq(discoveredLike::getDiscoveredId, discoveredId).one();
        if (discoveredLike1 == null){
            return Result.fail(Code.SYNTAX_ERROR,"该用户没有对该论坛点赞，无法取消");
        }
        //将论坛的获赞数取出并减1
        one.setLikesNumber(one.getLikesNumber() - 1);
        //将改变获赞数的论坛保存
        updateById(one);
        //从获赞表中删除
        discoveredLikeService.removeById(discoveredLike1.getId());
        return Result.success();
    }

    /**
     * 用户收藏
     * @param discoveredId
     * @param request
     * @return
     */
    @Override
    public Result discoveredCollection(Integer discoveredId, HttpServletRequest request) {
        UserDTO userDTO = getUserDTO(request);
        if (userDTO == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        discovered one = getDiscovered(discoveredId);
        if (one == null){
            return Result.fail(Code.SYNTAX_ERROR,"该论坛不存在");
        }

        return null;
    }

    /***********************  discoverServiceImpl内部方法 ***************************/
    /**
     * 登录校验
     * @param request
     * @return
     */
    public UserDTO getUserDTO(HttpServletRequest request){
        //获取请求头信息
        String header = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(header);
        if (entries.isEmpty()){
            return null;
        }
        //获取userDTO对象
        return BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
    }

    /**
     * 通过id判断论坛是否存在
     * @param discoveredId
     * @return
     */
    public discovered getDiscovered(Integer discoveredId){
        //通过id获取发现信息
        return lambdaQuery().eq(discovered::getId, discoveredId).one();
    }
}
