package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.lable;
import com.creative.dto.Result;

public interface LableService extends IService<lable> {
    Result insertLable(lable lable);
    Result selectLableAll();
    Result deleteLable(Integer id);
    Result updateLable(lable lable);
}
