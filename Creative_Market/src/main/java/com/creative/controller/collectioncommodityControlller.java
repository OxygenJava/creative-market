package com.creative.controller;

import com.creative.domain.collectioncommodity;
import com.creative.domain.collectionpost;
import com.creative.dto.Result;
import com.creative.service.collectioncommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collection/commodity")
@CrossOrigin(origins = "http://localhost:8080")
public class collectioncommodityControlller {

    @Autowired
    private collectioncommodityService collectioncommodityService;

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody collectioncommodity collectioncommodity){
        Result result = collectioncommodityService.ClickCollectioncommodity(collectioncommodity);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody collectioncommodity collectioncommodity){
        Result result = collectioncommodityService.CancelCollectioncommodity(collectioncommodity);
        return result;
    }

    @GetMapping("/{id}")
    public Result selectPostcoll(@PathVariable Integer id){
        Result result = collectioncommodityService.selectCollectioncommodity(id);
        return result;
    }

}
