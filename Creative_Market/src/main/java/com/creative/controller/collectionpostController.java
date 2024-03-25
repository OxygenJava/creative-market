package com.creative.controller;

import com.creative.domain.collectionpost;
import com.creative.domain.likepost;
import com.creative.dto.Result;
import com.creative.service.likepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collpost")
public class collectionpostController {
    @Autowired
    private com.creative.service.collectionpostService collectionpostService;

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody collectionpost collectionpost){
        Result result = collectionpostService.ClickPostcoll(collectionpost);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody collectionpost collectionpost){
        Result result = collectionpostService.CancelPostcoll(collectionpost);
        return result;
    }

    @GetMapping("/{id}")
    public Result selectPostcoll(@PathVariable Integer id){
        Result result = collectionpostService.selectPostcoll(id);
        return result;
    }
}
