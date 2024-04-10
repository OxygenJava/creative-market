package com.creative.controller;

import com.creative.domain.likecommodity;
import com.creative.domain.likepost;
import com.creative.dto.Result;
import com.creative.service.likecommodityService;
import com.creative.service.likepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/like/commodity")
@CrossOrigin
public class likecommodityController {

    @Autowired
    private likecommodityService likecommodityService;

    @PutMapping("/click")
    public Result ClickLikecommodity(@RequestBody likecommodity likecommodity,HttpServletRequest request){
        Result result = likecommodityService.ClickLikecommodity(likecommodity,request);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikecommodity(@RequestBody likecommodity likecommodity,HttpServletRequest request){
        Result result = likecommodityService.CancelLikecommodity(likecommodity,request);
        return result;
    }

    @GetMapping
    public Result selectLikecommodity(HttpServletRequest request){
        Result result = likecommodityService.selectLikecommodity(request);
        return result;
    }

}
