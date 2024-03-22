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
public class commodityController {
    @Autowired
    private commodityService commodityService;

    @GetMapping("{id}")
    public Result selectCommodityById(@PathVariable Integer id, HttpServletRequest request) throws IOException {
        return commodityService.selectCommodityById(id,request);
    }


}
