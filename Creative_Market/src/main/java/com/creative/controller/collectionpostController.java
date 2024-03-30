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

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody collectionpost collectionpost, HttpServletRequest request){
        Result result = collectionpostService.ClickCollectionpost(collectionpost,request);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody collectionpost collectionpost,HttpServletRequest request){
        Result result = collectionpostService.CancelCollectionpost(collectionpost,request);
        return result;
    }

    @GetMapping("/all")
    public Result selectAllpost(HttpServletRequest request){
        Result result = collectionpostService.selectAllpost(request);
        return result;
    }

    @GetMapping
    public Result selectCollectionpost(HttpServletRequest request){
        Result result = collectionpostService.selectCollectionpost(request);
        return result;
    }
}
