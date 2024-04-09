package com.creative.controller;


import com.creative.domain.lable;
import com.creative.dto.Result;
import com.creative.service.LableService;

import com.creative.service.impl.LableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lable")
@CrossOrigin
public class LableController {
    @Autowired
    private LableServiceImpl lableService;



    //查询所有的标签
    @GetMapping
    public Result selectLableAll(){
        Result result = lableService.selectLableAll();
        return result;
    }

    //添加标签
    @PostMapping
    public Result insertLable(@RequestBody lable lable) {
        Result insert = lableService.insertLable(lable);
        return insert;
    }

    //修改标签
    @PutMapping
    public Result updateLable(@RequestBody lable lable) {
        Result result = lableService.updateLable(lable);
        return result;
    }

    //删除标签
    @DeleteMapping("/{id}")
    public Result deleteLable(@PathVariable Integer id){
        Result result = lableService.deleteLable(id);
        return result;
    }

    @GetMapping("/{pageSize}/{pageNumber}")
    public Result selectPages(@PathVariable Integer pageSize,@PathVariable Integer pageNumber){
        Result result = lableService.selectPages(pageSize,pageNumber);
        return result;
    }

    @GetMapping("/{name}")
    public Result selectLike(@PathVariable String  name){
        Result result = lableService.selectLike(name);
        return result;
    }

}
