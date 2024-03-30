package com.creative.controller;

import com.creative.domain.addressInfo;
import com.creative.dto.Result;
import com.creative.service.addressInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/addressInfo")
@CrossOrigin
public class addressInfoController {
    @Autowired
    private addressInfoService addressInfoService;

    @PostMapping("/addressInfoAdd")
    public Result addressInfoAdd(@RequestBody addressInfo addressInfo, HttpServletRequest request){
        return addressInfoService.addressInfoAdd(addressInfo, request);
    }

    @GetMapping("/addressInfoSelectAllByUserId")
    public Result addressInfoSelectAllByUserId(HttpServletRequest request){
        return addressInfoService.addressInfoSelectAllByUserId(request);
    }

    @PutMapping("/addressInfoUpdate")
    public Result addressInfoUpdate(@RequestBody addressInfo addressInfo){
        return addressInfoService.addressInfoUpdate(addressInfo);
    }
    @DeleteMapping("/addressInfoDeleteById/{id}")
    public Result addressInfoDeleteById(@PathVariable Integer id){
        return addressInfoService.addressInfoDeleteById(id);
    }

    @GetMapping("/addressInfoSelectOneByAddresseeId/{addresseeId}")
    public Result addressInfoSelectOneByAddresseeId(@PathVariable Integer addresseeId){
        return addressInfoService.addressInfoSelectOneByAddresseeId(addresseeId);
    }
}
