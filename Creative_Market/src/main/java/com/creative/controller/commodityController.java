package com.creative.controller;

import com.creative.domain.commodity;
import com.creative.dto.Result;
import com.creative.service.commodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/commodity")
@CrossOrigin(origins = "http://localhost:8080")

public class commodityController {
    @Autowired
    private commodityService commodityService;

    @GetMapping("/{id}")
    public Result selectCommodityById(@PathVariable Integer id, HttpServletRequest request) throws IOException {
        return commodityService.selectCommodityById(id,request);
    }

    //发布
    @PutMapping
    public Result insertCom(@RequestBody commodity commodity){
        Result result = commodityService.insertCom(commodity);
        return result;
    }

    //删除
    @DeleteMapping("/{id}")
    public Result deleteCom(@PathVariable Integer id){
        Result result = commodityService.deleteCom(id);
        return result;
    }

    //修改
    @PostMapping
    public Result updateCom(@RequestBody commodity commodity){
        Result result = commodityService.updateCom(commodity);
        return result;
    }

    //查询所有
    @GetMapping
    public Result selectComAll(){
        Result result = commodityService.selectComAll();
        return result;
    }

    //根据商品id查询其所有标签
    @GetMapping("/lable/{id}")
    public Result selectComLable(@PathVariable Integer id){
        Result result = commodityService.selectComLable(id);
        return result;
    }

    //根据商品id查询其所有团队成员
    @GetMapping("/team/{id}")
    public Result selectComTeam(@PathVariable Integer id){
        Result result = commodityService.selectComTeam(id);
        return result;
    }
}
