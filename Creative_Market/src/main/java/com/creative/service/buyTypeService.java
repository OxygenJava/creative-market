package com.creative.service;

import com.creative.domain.buyType;
import com.creative.dto.Result;

import java.util.List;

public interface buyTypeService {
    Result buyTypeAdd(buyType buyType);

    Result buyTypeAddMore(List<buyType> buyTypes);


    Result buyTypeSelectAllTypeByCommodityId(Integer commodityId);

    Result buyTypeDeleteById(Integer id);

    Result buyTypeUpdate(buyType buyType);
}
