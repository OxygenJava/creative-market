package com.creative.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creative.domain.childComments;
import com.creative.domain.fatherComments;
import com.creative.domain.post;
import com.creative.domain.user;
import com.creative.dto.*;
import com.creative.mapper.childCommentsMapper;
import com.creative.mapper.fatherCommentsMapper;
import com.creative.mapper.postMapper;
import com.creative.service.CommentsService;
import com.creative.service.LableService;
import com.creative.service.userService;
import com.creative.utils.imgUtils;
import com.creative.utils.userHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private fatherCommentsMapper fatherCommentsMapper;
    @Autowired
    private childCommentsMapper childCommentsMapper;
    @Autowired
    private postMapper postMapper;
    @Value("${creativeMarket.iconImage}")
    private String iconImage;
    @Autowired
    private userService userService;
    /**
     * 发表父级评论
     *
     * @param fatherCommentsDTO
     * @return
     */
    @Override
    public Result publicationFatherComment(fatherCommentsDTO fatherCommentsDTO) {
        UserDTO user = userHolder.getUser();
        if (user == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        post post = postMapper.selectById(fatherCommentsDTO.getPostId());
        if (post == null){
            return Result.fail(Code.SYNTAX_ERROR,"该帖子不存在");
        }
        String content = fatherCommentsDTO.getContent();
        if (content == null || "".equals(content)) {
            return Result.fail(Code.SYNTAX_ERROR,"评论不能为空");
        }
        fatherComments fatherComments = new fatherComments();
        fatherComments.setPostId(fatherCommentsDTO.getPostId());
        fatherComments.setUserId(user.getId());
        fatherComments.setContent(content);
        fatherComments.setCreateTime(new Date().getTime());
        fatherCommentsMapper.insert(fatherComments);
        return Result.success("操作成功");
    }

    /**
     * 发表子级评论
     *
     * @param childCommentsDTO
     * @return
     */
    @Override
    public Result publicationChildComment(childCommentsDTO childCommentsDTO) {
        UserDTO user = userHolder.getUser();
        if (user == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        post post = postMapper.selectById(childCommentsDTO.getPostId());
        if (post == null){
            return Result.fail(Code.SYNTAX_ERROR,"该帖子不存在");
        }
        fatherComments fatherComments = fatherCommentsMapper.selectById(childCommentsDTO.getFatherCommentsId());
        if (fatherComments == null){
            return Result.fail(Code.SYNTAX_ERROR,"该父级评论不存在");
        }

        //判断要回复的对象是否存在
        if (childCommentsDTO.getTarget() != null){
            LambdaQueryWrapper<fatherComments> fatherCommentsLqw = new LambdaQueryWrapper<>();
            fatherCommentsLqw.eq(com.creative.domain.fatherComments::getUserId,childCommentsDTO.getTarget())
                                .eq(com.creative.domain.fatherComments::getId,childCommentsDTO.getFatherCommentsId());
            List<fatherComments> fatherComments1 = fatherCommentsMapper.selectList(fatherCommentsLqw);

            LambdaQueryWrapper<childComments> childCommentsLqw = new LambdaQueryWrapper<>();
            childCommentsLqw.eq(childComments::getUserId,childCommentsDTO.getTarget())
                    .eq(childComments::getFatherCommentsId,childCommentsDTO.getFatherCommentsId());
            List<childComments> childComments = childCommentsMapper.selectList(childCommentsLqw);
            if (fatherComments1.size() == 0 && childComments.size() == 0){
                return Result.fail(Code.SYNTAX_ERROR,"要回复的评论不存在");
            }
        }

        String content = childCommentsDTO.getContent();
        if (content == null || "".equals(content)) {
            return Result.fail(Code.SYNTAX_ERROR,"评论不能为空");
        }
        childComments childComments = new childComments();
        childComments.setUserId(user.getId());
        childComments.setTarget(childCommentsDTO.getTarget());
        childComments.setContent(childCommentsDTO.getContent());
        childComments.setFatherCommentsId(childCommentsDTO.getFatherCommentsId());
        childComments.setTarget(childCommentsDTO.getTarget());
        childComments.setCreateTime(new Date().getTime());
        childCommentsMapper.insert(childComments);
        return Result.success("操作成功");
    }

    /**
     * 分页查询评论
     * @param pageSize
     * @param pageNumber
     * @param postId
     * @return
     */
    @Override
    public Result getCommentByPage(Integer pageSize, Integer pageNumber,Integer postId) {
        //首先查询父级评论
        Page<fatherComments> fatherCommentsPage = new Page<>(pageSize,pageNumber);
        LambdaQueryWrapper<fatherComments> fatherCommentsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fatherCommentsLambdaQueryWrapper.eq(fatherComments::getPostId,postId);
        IPage<fatherComments> fatherCommentsIPage = fatherCommentsMapper.selectPage
                (fatherCommentsPage,fatherCommentsLambdaQueryWrapper);
        List<fatherComments> records = fatherCommentsIPage.getRecords();
        //返回给前端的集合
        List<getCommentByPageDTO> getCommentByPageDTOList = new ArrayList<>();

        for (fatherComments record : records) {
            getCommentByPageDTO commentByPageDTO = new getCommentByPageDTO();
            //获取父级评论者的信息
            user fatherUser = userService.getById(record.getUserId());
            commentByPageDTO.setFatherUserId(record.getUserId());
            //设置父级评论id
            commentByPageDTO.setFatherId(record.getId());
            //设置回复正文
            commentByPageDTO.setContent(record.getContent());
            //设置回复的帖子id
            commentByPageDTO.setPostId(record.getPostId());

            //处理用户信息
            //设置用户名
            commentByPageDTO.setUserNickName(fatherUser.getNickName());
            //设置头像
            try {
                commentByPageDTO.setUserIconImage(
                        imgUtils.encodeImageToBase64(iconImage+"\\"+fatherUser.getIconImage())
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<getCommentByPageChildDTO> getCommentByPageChildDTOList = new ArrayList<>();
            //查询该父级评论下的子级评论
            LambdaQueryWrapper<childComments> lqw = new LambdaQueryWrapper<>();
            lqw.eq(childComments::getFatherCommentsId,record.getId());
            List<childComments> childComments = childCommentsMapper.selectList(lqw);
            //循环子级评论
            for (childComments childComment : childComments) {
                getCommentByPageChildDTO getByPageChildDTO = new getCommentByPageChildDTO();
                getByPageChildDTO.setId(childComment.getId());
                getByPageChildDTO.setChildUserId(childComment.getUserId());
                getByPageChildDTO.setContent(childComment.getContent());
                //获取发布者信息
                user faBuUser = userService.lambdaQuery().eq(user::getId, childComment.getUserId()).one();
                getByPageChildDTO.setUserNickName(faBuUser.getNickName());
//                设置头像
                try {
                    getByPageChildDTO.setUserIconImage(
                            imgUtils.encodeImageToBase64(iconImage+"\\"+faBuUser.getIconImage())
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (childComment.getTarget() != null){
                    //获取目标回复者信息
                    user targetUser = userService.lambdaQuery().eq(user::getId, childComment.getTarget()).one();
                    getByPageChildDTO.setTargetNickName(targetUser.getNickName());
                }
                getByPageChildDTO.setCreateTime(getTimeStr(childComment.getCreateTime()));
                getCommentByPageChildDTOList.add(getByPageChildDTO);
            }
            commentByPageDTO.setCreateTime(getTimeStr(record.getCreateTime()));
            commentByPageDTO.setGetCommentByPageChildDTOList(getCommentByPageChildDTOList);
            commentByPageDTO.setTotal(getCommentByPageChildDTOList.size());
            getCommentByPageDTOList.add(commentByPageDTO);
        }
        System.out.println(getCommentByPageDTOList.size());
        return Result.success(getCommentByPageDTOList);
    }

    /**
     * 获取该帖子的父级标签总数
     *
     * @param postId
     * @return
     */
    @Override
    public Result getTotalNumber(Integer postId) {
        LambdaQueryWrapper<fatherComments> fatherCommentsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fatherCommentsLambdaQueryWrapper.eq(fatherComments::getPostId,postId);
        List<fatherComments> fatherComments = fatherCommentsMapper.selectList(fatherCommentsLambdaQueryWrapper);
        return Result.success(fatherComments.size());
    }

    /**
     * 获取发布时间与当前时间之差
     * @param releasedTime
     * @return
     */
    private String getTimeStr(Long releasedTime) {
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
        }else if (time / 60 / 24 > 0){
            //当相差的时间/60 / 24大于0，表示可以使用天为单位
            timeStr = time / 60 / 24 + "天前";
        }else if (time / 60 > 0){
            //当相差的时间/60大于0，表示可以使用小时为单位
            timeStr = time / 60+"小时前";
        }else {
            timeStr = time + "分钟前";
        }
        return timeStr;
    }

}
