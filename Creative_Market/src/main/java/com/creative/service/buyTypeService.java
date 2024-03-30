package com.creative.service;

import com.creative.domain.buyType;
import com.creative.dto.Result;

public interface buyTypeService {
    Result buyTypeAdd(buyType buyType);


    Result buyTypeSelectAllTypeByCommodityId(Integer commodityId);

    Result buyTypeDeleteById(Integer id);

    Result buyTypeUpdate(buyType buyType);
}
