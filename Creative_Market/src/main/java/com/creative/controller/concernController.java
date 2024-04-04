package com.creative.controller;

import com.creative.domain.concern;
import com.creative.domain.post;
import com.creative.dto.Result;
import com.creative.service.concernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/concern")
public class concernController {

    @Autowired
    private concernService concernService;

    @PostMapping
    public Result concernPerson(@RequestBody concern concern, HttpServletRequest request){
        Result result = concernService.concernPerson(concern,request);
        return result;
    }

    @DeleteMapping
    public Result cancelConcern(@RequestBody concern concern,HttpServletRequest request){
        Result result = concernService.cancelConcern(concern,request);
        return result;
    }

    @GetMapping
    public Result countConcern(HttpServletRequest request){
        Result result = concernService.countConcern(request);
        return result;
    }

}
