package com.creative.controller;

import com.creative.domain.pay;
import com.creative.dto.Result;
import com.creative.service.payService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pay")
public class payController {
    @Autowired
    private payService payService;

    @PostMapping("/payAdd")
    public Result payAdd(@RequestBody pay pay, HttpServletRequest request){
        return payService.payAdd(pay,request);
    }

    @GetMapping("/paySelect/{commodityId}")
    public Result paySelect(@PathVariable Integer commodityId,HttpServletRequest request){
        return payService.paySelect(commodityId,request);
    }
}
