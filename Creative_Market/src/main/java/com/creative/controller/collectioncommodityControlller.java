package com.creative.controller;

import com.creative.domain.collectioncommodity;
import com.creative.domain.collectionpost;
import com.creative.dto.Result;
import com.creative.service.collectioncommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/collection/commodity")
@CrossOrigin
public class collectioncommodityControlller {

    @Autowired
    private collectioncommodityService collectioncommodityService;

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody collectioncommodity collectioncommodity, HttpServletRequest request){
        Result result = collectioncommodityService.ClickCollectioncommodity(collectioncommodity,request);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody collectioncommodity collectioncommodity,HttpServletRequest request){
        Result result = collectioncommodityService.CancelCollectioncommodity(collectioncommodity,request);
        return result;
    }

    @GetMapping("/all")
    public Result selectAllcommodity(HttpServletRequest request){
        Result result = collectioncommodityService.selectAllcommodity(request);
        return result;
    }

    @GetMapping
    public Result selectCollectioncommodity(HttpServletRequest request){
        Result result = collectioncommodityService.selectCollectioncommodity(request);
        return result;
    }

}
