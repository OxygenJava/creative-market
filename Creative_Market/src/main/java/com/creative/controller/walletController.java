package com.creative.controller;

import com.creative.dto.Result;
import com.creative.dto.payPasswordForm;
import com.creative.service.walletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin
public class walletController {
    @Autowired
    private walletService walletService;

    @GetMapping("/isOpenWallet")
    public Result isOpenWallet(HttpServletRequest request){
        return walletService.isOpenWallet(request);
    }

    @PostMapping("/openWallet")
    public Result openWallet(@RequestBody payPasswordForm payPasswordForm,HttpServletRequest request){
        return walletService.openWallet(payPasswordForm, request);
    }

    @PutMapping("/updatePayPassword")
    public Result updatePayPassword(@RequestBody payPasswordForm payPasswordForm, HttpServletRequest request){
        return walletService.updatePayPassword(payPasswordForm,request);
    }
    @GetMapping("/selectWallet")
    public Result selectWallet(HttpServletRequest request){
        return walletService.selectWallet(request);
    }

    @PutMapping("/investMoney/{investMoney}")
    public Result investMoney(@PathVariable BigDecimal investMoney,HttpServletRequest request){
        return walletService.investMoney(investMoney,request);
    }
}
