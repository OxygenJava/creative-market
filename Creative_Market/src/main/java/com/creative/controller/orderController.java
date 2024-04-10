package com.creative.controller;

import com.creative.domain.orderTable;
import com.creative.dto.Result;
import com.creative.service.orderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class orderController {
    @Autowired
    private orderService orderService;

    @PostMapping("/orderAdd")
    public Result orderAdd(@RequestBody orderTable orderTable, HttpServletRequest request){
        return orderService.orderAdd(orderTable, request);
    }

    @GetMapping("/orderSelectByUserId")
    public Result orderSelectByUserId(HttpServletRequest request){
        return orderService.orderSelectByUserId(request);
    }

    @GetMapping("/orderSelectOneByOrderId/{orderId}")
    public Result orderSelectOneByOrderId(@PathVariable Integer orderId){
        return orderService.orderSelectOneByOrderId(orderId);
    }

    @PutMapping("/orderUpdateById")
    public Result orderUpdateById(@RequestBody orderTable orderTable){
        return orderService.orderUpdateById(orderTable);
    }

    @PutMapping("/orderPay/{orderId}")
    public Result orderPay(@PathVariable Integer orderId,HttpServletRequest request){
        return orderService.orderPay(orderId,request);
    }

    @DeleteMapping("/orderDelete/{orderId}")
    public Result orderDelete(@PathVariable Integer orderId){
        return orderService.orderDelete(orderId);
    }
}
