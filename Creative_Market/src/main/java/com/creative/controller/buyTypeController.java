package com.creative.controller;

import com.creative.domain.buyType;
import com.creative.dto.Result;
import com.creative.service.buyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyType")
public class buyTypeController {

    @Autowired
    private buyTypeService buyTypeService;


    @PostMapping("/buyTypeAdd")
    public Result buyTypeAdd(@RequestBody buyType buyType){
        return buyTypeService.buyTypeAdd(buyType);
    }

    @GetMapping("/buyTypeSelectAll")
    public Result buyTypeSelectAll(){
        return buyTypeService.buyTypeSelectAll();
    }
}
