package com.creative.service;

import com.creative.domain.collectioncommodity;
import com.creative.domain.collectionpost;
import com.creative.dto.Result;

public interface collectioncommodityService {
    Result ClickCollectioncommodity(collectioncommodity collectioncommodity);
    Result CancelCollectioncommodity(collectioncommodity collectioncommodity);
    Result selectCollectioncommodity(Integer id);
}
