package com.creative.controller;

import com.creative.domain.likepost;
import com.creative.domain.post;
import com.creative.dto.Result;
import com.creative.service.likepostService;
import com.creative.service.postService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/like/post")
@CrossOrigin
public class likepostController {
    @Autowired
    private likepostService likepostService;

    /**
     * 帖子点赞
     * @param postId
     * @param request
     * @return
     */
    @PutMapping("/click/{postId}")
    public Result ClickLikes(@PathVariable Integer postId, HttpServletRequest request){
        Result result = likepostService.ClickLikepost(postId,request);
        return result;
    }

    /**
     * 取消点赞
     * @param postId
     * @param request
     * @return
     */
    @PutMapping("/cancel/{postId}")
    public Result CancelLikes(@PathVariable Integer postId,HttpServletRequest request){
        Result result = likepostService.CancelLikepost(postId,request);
        return result;
    }

    //根据用户id查询该用户点赞过和为未点赞的所有帖子
    @GetMapping("/all")
    public Result selectAllpost(HttpServletRequest request){
        Result result = likepostService.selectAllpost(request);
        return result;
    }

    //根据用户id查询该用户点赞过的帖子
    @GetMapping
    public Result selectLikepost(HttpServletRequest request){
        Result result = likepostService.selectLikepost(request);
        return result;
    }
}
