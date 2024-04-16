package com.creative.controller;

import com.creative.domain.collectionpost;
import com.creative.domain.likepost;
import com.creative.dto.Result;
import com.creative.service.likepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.creative.service.collectionpostService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/collection/post")
@CrossOrigin
public class collectionpostController {
    @Autowired
    private collectionpostService collectionpostService;

    //收藏帖子
    @PutMapping("/click/{postId}")
    public Result ClickLikes(@PathVariable Integer postId, HttpServletRequest request){
        Result result = collectionpostService.ClickCollectionpost(postId,request);
        return result;
    }

    //取消收藏
    @PutMapping("/cancel/{postId}")
    public Result CancelLikes(@PathVariable Integer postId,HttpServletRequest request){
        Result result = collectionpostService.CancelCollectionpost(postId,request);
        return result;
    }


    //根据用户id查询该用户收藏过的帖子
    @GetMapping
    public Result selectCollectionpost(HttpServletRequest request){
        Result result = collectionpostService.selectCollectionpost(request);
        return result;
    }

    //获取收藏的帖子和商品的收藏总数
    @GetMapping("/total")
    public Result selectCollectionTotal(HttpServletRequest request){
        Result result = collectionpostService.selectCollectionTotal(request);
        return result;
    }
}
