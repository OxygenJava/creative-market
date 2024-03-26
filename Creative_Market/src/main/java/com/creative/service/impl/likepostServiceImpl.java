package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.mapper.likepostMapper;
import com.creative.mapper.postMapper;
import com.creative.service.likepostService;
import com.mysql.cj.xdevapi.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class likepostServiceImpl implements likepostService {

    @Autowired
    private postMapper postMapper;

    @Autowired
    private likepostMapper likepostMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Result ClickLikepost(likepost likepost) {

//        String authorization = request.getHeader("Authorization");
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
//        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
//        likepost.setUid(user.getId());

        if(likepost.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            post post = postMapper.selectById(likepost.getPid());
            post.setLikes(post.getLikes()+1);
            post.setLikesState(1);
            int update = postMapper.updateById(post);
            int insert = likepostMapper.insert(likepost);
            Integer code = update > 0 && insert>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && insert>0? "点赞成功" : "点赞失败";
            return new Result(code, msg, "");
        }

    }

    @Override
    public Result CancelLikepost(likepost likepost) {

        //        String authorization = request.getHeader("Authorization");
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
//        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
//        likepost.setUid(user.getId());


        if(likepost.getUid()==null){
            return new Result(Code.INSUFFICIENT_PERMISSIONS,"请先登录","");
        }
        else {
            post post = postMapper.selectById(likepost.getPid());
            post.setLikes(post.getLikes()-1);
            post.setLikesState(0);
            int update = postMapper.updateById(post);
            int delete = likepostMapper.deleteBylikepost(likepost);

            Integer code = update > 0 && delete>0? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 && delete>0? "取消点赞成功" : "取消点赞失败";
            return new Result(code, msg, "");
        }

    }

    @Override
    public Result selectLikepost(Integer id) {

        //        String authorization = request.getHeader("Authorization");
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
//        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
//        likepost.setUid(user.getId());


        LambdaQueryWrapper<likepost> lqw=new LambdaQueryWrapper<>();
        lqw.eq(likepost::getUid,id);
        List<likepost> likeposts = likepostMapper.selectList(lqw);
        ArrayList<Integer> list1=new ArrayList<>();
        ArrayList<Integer> list2=new ArrayList<>();
        ArrayList<post> list=new ArrayList<>();
       if(likeposts==null){
           List<post> posts1 = postMapper.selectList(null);
           for (int i = 0; i < posts1.size(); i++) {
               posts1.get(i).setLikesState(0);
           }
           Integer code = posts1 !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
           String msg = posts1 !=null? "查询成功" : "查询失败";
           return new Result(code, msg, posts1);
       }
           else{

           for (int i = 0; i < likeposts.size(); i++) {
               list1.add(likeposts.get(i).getPid());
           }
           List<post> posts1=new ArrayList<>();
           for (int i = 0; i < list1.size(); i++) {
               LambdaQueryWrapper<post> lqw1=new LambdaQueryWrapper<>();
               lqw1.eq(post::getId,list1.get(i));
               post post = postMapper.selectOne(lqw1);
               posts1.add(post);
           }

           if(posts1!=null){
               for (int i = 0; i < posts1.size(); i++) {
                   posts1.get(i).setLikesState(1);
               }
           }



           List<post> posts2=postMapper.selectList(null);
           for (int i = 0; i < posts2.size(); i++) {
               list2.add(posts2.get(i).getId());
           }

            list2.removeAll(list1);

           List<post> posts3=new ArrayList<>();
           for (int i = 0; i < list2.size(); i++) {
               LambdaQueryWrapper<post> lqw2=new LambdaQueryWrapper<>();
               lqw2.eq(post::getId,list2.get(i));
               post post = postMapper.selectOne(lqw2);
               posts3.add(post);
           }
           if(posts3!=null){
               for (int i = 0; i < posts3.size(); i++) {
                   posts3.get(i).setLikesState(0);
               }
           }
           list.addAll(posts1);
           list.addAll(posts3);
       }


           Integer code = list !=null ? Code.NORMAL : Code.SYNTAX_ERROR;
           String msg = list !=null? "查询成功" : "查询失败";
           return new Result(code, msg, list);
           }

       }



