package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.*;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.dto.postDTO;
import com.creative.mapper.*;
import com.creative.service.collectionpostService;
import com.creative.service.userService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class collectionpostServiceImpl implements collectionpostService {

    @Autowired
    private postMapper postMapper;
    @Autowired
    private likepostMapper likepostMapper;
    @Autowired
    private collectionpostMapper collectionpostMapper;
    @Autowired
    private userService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private collectioncommodityMapper collectioncommodityMapper;

    @Autowired
    private commodityMapper commodityMapper;

    @Value("${creativeMarket.discoverImage}")
    private String discoverImage;
    @Value("${creativeMarket.iconImage}")
    private String iconImage;


    @Override
    public Result ClickCollectionpost(Integer postId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        if (entries.isEmpty()) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        }

        UserDTO user = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);


        post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail(Code.SYNTAX_ERROR, "帖子不存在");
        }
        //查询收藏表
        LambdaQueryWrapper<collectionpost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(collectionpost::getUid, user.getId()).eq(collectionpost::getPid, postId);
        collectionpost collectionpost = collectionpostMapper.selectOne(lqw);
        if (collectionpost != null) {
            return Result.fail(Code.SYNTAX_ERROR, "该帖子您已经收藏过了");
        }

        //增加帖子收藏数
        post.setCollection(post.getCollection() + 1);
        post.setCollectionState(1);
        int update = postMapper.updateById(post);

        //插入收藏表
        collectionpost = new collectionpost();
        collectionpost.setUid(user.getId());
        collectionpost.setPid(postId);
        collectionpost.setCreateTime(LocalDateTime.now());
        int insert = collectionpostMapper.insert(collectionpost);
        Integer code = update > 0 && insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && insert > 0 ? "收藏成功" : "收藏失败";
        return new Result(code, msg, "");


    }

    @Override
    public Result CancelCollectionpost(Integer postId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        if (entries.isEmpty()) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        }

        UserDTO user = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);

        post post = postMapper.selectById(postId);
        if (post == null){
            return Result.fail(Code.SYNTAX_ERROR, "帖子不存在");
        }
        //查询收藏表
        LambdaQueryWrapper<collectionpost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(collectionpost::getUid, user.getId()).eq(collectionpost::getPid, postId);
        collectionpost collectionpost = collectionpostMapper.selectOne(lqw);
        if (collectionpost == null) {
            return Result.fail(Code.SYNTAX_ERROR, "该帖子您还没有收藏,无法取消");
        }

        post.setCollection(post.getCollection() - 1);
        post.setCollectionState(0);
        int update = postMapper.updateById(post);
        int insert = collectionpostMapper.deleteBycollpost(collectionpost);
        Integer code = update > 0 && insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && insert > 0 ? "取消收藏成功" : "取消收藏失败";
        return new Result(code, msg, "");
    }


    @Override
    public Result selectCollectionpost(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        LambdaQueryWrapper<collectionpost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(collectionpost::getUid, user.getId());
        List<collectionpost> collectionposts = collectionpostMapper.selectList(lqw);
        ArrayList<Integer> list1 = new ArrayList<>();

        ArrayList<postDTO> list = new ArrayList<>();

        if (collectionposts == null) {

            return new Result(Code.SYNTAX_ERROR, "", "");
        } else {
            for (int i = 0; i < collectionposts.size(); i++) {
                list1.add(collectionposts.get(i).getPid());
            }
            List<postDTO> posts2 = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                LambdaQueryWrapper<post> lqw1 = new LambdaQueryWrapper<>();
                lqw1.eq(post::getId, list1.get(i));
                post post = postMapper.selectOne(lqw1);
                postDTO postDTO = BeanUtil.copyProperties(post, postDTO.class);
                //设置点赞状态，查询帖子点赞表
                LambdaQueryWrapper<likepost> likepostLambdaQueryWrapper = new LambdaQueryWrapper<>();
                likepostLambdaQueryWrapper.eq(likepost::getUid,user.getId()).eq(likepost::getPid,post.getId());
                likepost likepost = likepostMapper.selectOne(likepostLambdaQueryWrapper);
                if (likepost == null){
                    postDTO.setLikesState(0);
                }else {
                    postDTO.setLikesState(1);
                }

                List<String> image = postDTO.getImage();
                for (int i1 = 0; i1 < image.size(); i1++) {
                    try {
                        image.set(i1, imgUtils.encodeImageToBase64(discoverImage+"\\"+image.get(i1)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                postDTO.setImage(image);

                Integer uid = postDTO.getUid();
                user userOne = userService.lambdaQuery().eq(com.creative.domain.user::getId, uid).one();
                postDTO.setPostUserNickName(userOne.getNickName());
                try {
                    postDTO.setIconImage(imgUtils.encodeImageToBase64(iconImage+"\\"+userOne.getIconImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                posts2.add(postDTO);
            }
            if (posts2 != null) {
                for (int i = 0; i < posts2.size(); i++) {
                    posts2.get(i).setCollectionState(1);
                }
            }
            list.addAll(posts2);
        }
        Integer code = list != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = list != null ? "查询成功" : "查询失败";
        return new Result(code, msg, list);

    }

    @Override
    public Result selectCollectionTotal(HttpServletRequest request) {
        Integer collectionPostTotal=0;
        Integer collectioncommodityTotal=0;
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        if (entries.isEmpty()) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        }
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        //帖子收藏总数
        LambdaQueryWrapper<collectionpost> lqw=new LambdaQueryWrapper<>();
        lqw.eq(collectionpost::getUid,userDTO.getId());
        List<collectionpost> collectionposts = collectionpostMapper.selectList(lqw);
        if(collectionposts!=null){

                collectionPostTotal+=collectionposts.size();

        }
        else {
            collectionPostTotal+=0;
        }
        //商品收藏总数
        LambdaQueryWrapper<collectioncommodity> lqw1=new LambdaQueryWrapper<>();
        lqw1.eq(collectioncommodity::getUid,userDTO.getId());
        List<collectioncommodity> collectioncommodities = collectioncommodityMapper.selectList(lqw1);
        if(collectioncommodities!=null){

                collectioncommodityTotal+=collectioncommodities.size();

        }
        else {
            collectioncommodityTotal+=0;
        }

        return new Result(Code.NORMAL, "", collectioncommodityTotal+collectionPostTotal);
    }
}
