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
@CrossOrigin
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

    @DeleteMapping("/fans")
    public Result cancelFans(@RequestBody concern concern,HttpServletRequest request){
        Result result = concernService.cancelFans(concern,request);
        return result;
    }

    @GetMapping("/if/{uid}")
    public Result ifconcern(@PathVariable Integer uid, HttpServletRequest request){
        Result result = concernService.ifconcern(uid,request);
        return result;
    }

    @GetMapping("/fans/{pageSize}/{pageNumber}")
    public Result ObtainFans(@PathVariable Integer pageSize,@PathVariable Integer pageNumber,HttpServletRequest request){
        Result result = concernService.ObtainFans(pageSize, pageNumber, request);
        return result;
    }


    @GetMapping("/focus/{pageSize}/{pageNumber}")
    public Result ObtainFocus(@PathVariable Integer pageSize,@PathVariable Integer pageNumber,HttpServletRequest request){
        Result result = concernService.ObtainFocus(pageSize, pageNumber, request);
        return result;
    }


    @GetMapping("/Likefocus/{name}")
    public Result selectLikeFocus(@PathVariable String name,HttpServletRequest request){
        Result result = concernService.selectLikeFocus(name,request);
        return result;
    }


    @GetMapping("/Likefans/{name}")
    public Result selectLikeFans(@PathVariable String name,HttpServletRequest request){
        Result result = concernService.selectLikeFans(name,request);
        return result;
    }

    @GetMapping("/fansTotal")
    public Result selectFansTotal(HttpServletRequest request){
        Result result = concernService.selectFansTotal(request);
        return result;
    }

    @GetMapping("/focusTotal")
    public Result selectFocusTotal(HttpServletRequest request){
        Result result = concernService.selectFocusTotal(request);
        return result;
    }

}
