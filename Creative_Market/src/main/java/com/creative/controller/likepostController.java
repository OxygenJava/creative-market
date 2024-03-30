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

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody likepost likepost, HttpServletRequest request){
        Result result = likepostService.ClickLikepost(likepost,request);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody likepost likepost,HttpServletRequest request){
        Result result = likepostService.CancelLikepost(likepost,request);
        return result;
    }

    @GetMapping("/all")
    public Result selectAllpost(HttpServletRequest request){
        Result result = likepostService.selectAllpost(request);
        return result;
    }

    @GetMapping
    public Result selectLikepost(HttpServletRequest request){
        Result result = likepostService.selectLikepost(request);
        return result;
    }
}
