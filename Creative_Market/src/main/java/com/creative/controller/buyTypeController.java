package com.creative.controller;

import com.creative.domain.buyType;
import com.creative.dto.Result;
import com.creative.service.buyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyType")
@CrossOrigin
public class buyTypeController {

    @Autowired
    private buyTypeService buyTypeService;


    @PostMapping("/buyTypeAdd")
    public Result buyTypeAdd(@RequestBody buyType buyType){
        return buyTypeService.buyTypeAdd(buyType);
    }

    @GetMapping("/buyTypeSelectAllTypeByCommodityId/{commodityId}")
    public Result buyTypeSelectAllTypeByCommodityId(@PathVariable Integer commodityId){
        return buyTypeService.buyTypeSelectAllTypeByCommodityId(commodityId);
    }

    @DeleteMapping("/buyTypeDeleteById/{id}")
    public Result buyTypeDeleteById(@PathVariable Integer id){
        return buyTypeService.buyTypeDeleteById(id);
    }

    @PutMapping("buyTypeUpdate")
    public Result buyTypeUpdate(@RequestBody buyType buyType){
        return buyTypeService.buyTypeUpdate(buyType);
    }
}
