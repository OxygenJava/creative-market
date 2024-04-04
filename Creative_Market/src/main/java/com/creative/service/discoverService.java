package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.discovered;
import com.creative.dto.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface discoverService extends IService<discovered> {
    /**
     * 发布论坛
     * @param file
     * @param disc
     * @param request
     * @return
     */
    Result uploadDiscover(MultipartFile[] file, discovered disc, HttpServletRequest request) throws IOException;

    /**
     * 分页获取发现中内容
     * @param pageSize
     * @param pageNumber
     * @return
     */
    Result getAllDiscover(int pageSize, int pageNumber) throws IOException;

    /**
     * 用户点赞
     * @param request
     * @param discoveredId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    Result discoveredLike(HttpServletRequest request, Integer discoveredId);

    /**
     * 用户取消点赞
     * @param request
     * @param discoveredId
     * @return
     */
    Result cancelDiscoveredLike(HttpServletRequest request, Integer discoveredId);

    /**
     * 用户收藏
     * @param discoveredId
     * @param request
     * @return
     */
    Result discoveredCollection(Integer discoveredId, HttpServletRequest request);
}
