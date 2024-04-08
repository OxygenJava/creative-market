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

    //点赞帖子
    @PutMapping("/click")
    public Result ClickLikes(@RequestBody likepost likepost, HttpServletRequest request){
        Result result = likepostService.ClickLikepost(likepost,request);
        return result;
    }

    //取消点赞
    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody likepost likepost,HttpServletRequest request){
        Result result = likepostService.CancelLikepost(likepost,request);
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
