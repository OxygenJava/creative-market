package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.discovered;
import com.creative.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface discoverService extends IService<discovered> {
    /**
     * 发布论坛
     * @param file
     * @param disc
     * @param request
     * @return
     */
    Result uploadDiscover(MultipartFile[] file, discovered disc, HttpServletRequest request);
}
