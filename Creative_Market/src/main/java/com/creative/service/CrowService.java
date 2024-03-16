package com.creative.service;

import com.creative.domain.crow;
import com.creative.dto.Result;

public interface CrowService {
    Result Crowinsert(crow crow);
    Result CrowselectAll();

}
