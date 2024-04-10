package com.creative.controller;

import com.creative.dto.Result;
import com.creative.service.payService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pay")
@CrossOrigin
public class payController {
    @Autowired
    private payService payService;

    @GetMapping("/paySelect/{commodityId}/{buyTypeId}")
    public Result paySelect(@PathVariable Integer commodityId,@PathVariable Integer buyTypeId,HttpServletRequest request){
        return payService.paySelect(commodityId,buyTypeId,request);
    }
}
