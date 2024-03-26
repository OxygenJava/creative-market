package com.creative.controller;

import com.creative.domain.collectionpost;
import com.creative.domain.likepost;
import com.creative.dto.Result;
import com.creative.service.likepostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.creative.service.collectionpostService;

@RestController
@RequestMapping("/api/collection/post")
@CrossOrigin(origins = "http://localhost:8080")

public class collectionpostController {
    @Autowired
    private collectionpostService collectionpostService;

    @PutMapping("/click")
    public Result ClickLikes(@RequestBody collectionpost collectionpost){
        Result result = collectionpostService.ClickCollectionpost(collectionpost);
        return result;
    }

    @PutMapping("/cancel")
    public Result CancelLikes(@RequestBody collectionpost collectionpost){
        Result result = collectionpostService.CancelCollectionpost(collectionpost);
        return result;
    }

    @GetMapping("/{id}")
    public Result selectPostcoll(@PathVariable Integer id){
        Result result = collectionpostService.selectCollectionpost(id);
        return result;
    }
}
