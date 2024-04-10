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
import java.time.LocalDateTime;
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

    /**
     * 帖子点赞
     * @param postId
     * @param request
     * @return
     */
    @Override
    public Result ClickLikepost(Integer postId, HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);

        if (entries.isEmpty()) {
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS, "请登录");
        }
        if (!isTruePost(postId)) {
            return Result.fail(Code.SYNTAX_ERROR, "帖子不存在");
        }

        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        likepost likepost = new likepost();
        likepost.setUid(user.getId());
        likepost.setPid(postId);
        //查询用户是否已经对该帖子点赞
        LambdaQueryWrapper<likepost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(com.creative.domain.likepost::getUid, user.getId())
                .eq(com.creative.domain.likepost::getPid, likepost.getPid());
        likepost likepostOne = likepostMapper.selectOne(lqw);
        if (likepostOne != null) {
            return Result.fail(Code.SYNTAX_ERROR, "已经对该帖子点过赞了");
        }

        post post = postMapper.selectById(likepost.getPid());
        post.setLikes(post.getLikes() + 1);
        post.setLikesState(1);
        likepost.setCreateTime(LocalDateTime.now());
        int update = postMapper.updateById(post);
        int insert = likepostMapper.insert(likepost);
        Integer code = update > 0 && insert > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && insert > 0 ? "点赞成功" : "点赞失败";
        return new Result(code, msg);
    }

    /**
     * 取消点赞
     * @param postId
     * @param request
     * @return
     */
    @Override
    public Result CancelLikepost(Integer postId, HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        if (entries.isEmpty()) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        }
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);


        post post = postMapper.selectById(postId);

        //查询点赞表
        LambdaQueryWrapper<likepost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(likepost::getUid,user.getId()).eq(likepost::getPid,postId);
        likepost likepost = likepostMapper.selectOne(lqw);
        if (post == null){
            return Result.fail(Code.SYNTAX_ERROR, "帖子不存在");
        }
        if (likepost == null){
            return Result.fail(Code.SYNTAX_ERROR,"您还未对该帖子点赞过");
        }
        post.setLikes(post.getLikes() - 1);
        post.setLikesState(0);
        int update = postMapper.updateById(post);
        int delete = likepostMapper.deleteBylikepost(likepost);
        Integer code = update > 0 && delete > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = update > 0 && delete > 0 ? "取消点赞成功" : "取消点赞失败";
        return new Result(code, msg);
    }


    @Override
    public Result selectLikepost(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        LambdaQueryWrapper<likepost> lqw = new LambdaQueryWrapper<>();
        lqw.eq(likepost::getUid, user.getId());
        List<likepost> likeposts = likepostMapper.selectList(lqw);

        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<post> list = new ArrayList<>();

        if (likeposts == null) {
            return new Result(Code.SYNTAX_ERROR, "", "");
        } else {
            for (int i = 0; i < likeposts.size(); i++) {
                list1.add(likeposts.get(i).getPid());
            }
            List<post> posts1 = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                LambdaQueryWrapper<post> lqw1 = new LambdaQueryWrapper<>();
                lqw1.eq(post::getId, list1.get(i));
                post post = postMapper.selectOne(lqw1);
                posts1.add(post);
            }

            if (posts1 != null) {
                for (int i = 0; i < posts1.size(); i++) {
                    posts1.get(i).setLikesState(1);
                }
            }

            list.addAll(posts1);
        }

        Integer code = list != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = list != null ? "查询成功" : "查询失败";
        return new Result(code, msg, list);
    }

    /**
     * 判断帖子是否存在
     *
     * @param id
     * @return
     */
    public Boolean isTruePost(Integer id) {
        post post = postMapper.selectById(id);
        if (post == null) {
            return false;
        } else {
            return true;
        }
    }
}



