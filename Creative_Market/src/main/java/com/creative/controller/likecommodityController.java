package com.creative.controller;

import com.creative.domain.likecommodity;
import com.creative.domain.likepost;
import com.creative.dto.Result;
import com.creative.service.likecommodityService;
import com.creative.service.likepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like/commodity")
@CrossOrigin
public class likecommodityController {

    @Autowired
    private likecommodityService likecommodityService;

    @PutMapping("/click")
    public Result ClickLikecommodity(@RequestBody likecommodity likecommodity){
        Result result = likecommodityService.ClickLikecommodity(likecommodity);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikecommodity(@RequestBody likecommodity likecommodity){
        Result result = likecommodityService.CancelLikecommodity(likecommodity);
        return result;
    }

    @GetMapping("/{id}")
    public Result selectLikecommodity(@PathVariable Integer id){
        Result result = likecommodityService.selectLikecommodity(id);
        return result;
    }

}
