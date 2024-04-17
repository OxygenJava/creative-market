package com.creative.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.creative.domain.commodity;
import com.creative.dto.Result;
import com.creative.dto.commodityDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public interface commodityService extends IService<commodity> {
    @Transactional(rollbackFor = Exception.class)
    Result selectCommodityById(Integer id, HttpServletRequest request) throws IOException;
    @Transactional(rollbackFor = Exception.class)
    Result insertCom(MultipartFile[] file, commodityDTO commodityDTO, HttpServletRequest request) throws IOException;
    Result deleteCom(Integer id);
    Result updateCom(commodity commodity);
    Result selectComAll();
    Result selectComLable(Integer id);
    Result selectComTeam(Integer id);
    Result selectByUidAllCommodity(HttpServletRequest request);

}
