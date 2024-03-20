package com.creative.service;

import com.creative.domain.crow;
import com.creative.domain.lable;
import com.creative.dto.Result;

public interface LableService {
    Result insertLable(lable lable);
    Result selectLableAll();
    Result deleteLable(Integer id);
    Result updateLable(lable lable);
    Result selectByName(Integer id);
}
