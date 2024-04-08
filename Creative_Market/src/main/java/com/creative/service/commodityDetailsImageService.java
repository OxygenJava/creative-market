package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodityDetailsImage;
import com.creative.dto.Result;

public interface commodityDetailsImageService extends IService<commodityDetailsImage> {
    Result getAllByCommodityId(Integer commodityId);
}
