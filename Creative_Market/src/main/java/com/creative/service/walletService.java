package com.creative.service;

import com.creative.dto.Result;
import com.creative.dto.payPasswordForm;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface walletService {
    Result isOpenWallet(HttpServletRequest request);
    Result openWallet(payPasswordForm payPasswordForm,HttpServletRequest request);
    Result updatePayPassword(payPasswordForm payPasswordForm,HttpServletRequest request);
    Result selectWallet(HttpServletRequest request);
    Result investMoney(BigDecimal investMoney,HttpServletRequest request);
}
