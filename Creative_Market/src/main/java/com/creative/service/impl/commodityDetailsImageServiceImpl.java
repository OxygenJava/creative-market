package com.creative.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.commodityDetailsImage;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.mapper.commodityDetailsImageMapper;
import com.creative.service.commodityDetailsImageService;
import com.creative.utils.imgUtils;
import com.creative.utils.userHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class commodityDetailsImageServiceImpl extends ServiceImpl<commodityDetailsImageMapper, commodityDetailsImage>
        implements commodityDetailsImageService {

    @Value("${creativeMarket.detailsImage}")
    private String detailsImageAddress;

    @Override
    public Result getAllByCommodityId(Integer commodityId) {

        UserDTO user = userHolder.getUser();
        if (user == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }

        List<commodityDetailsImage> list = lambdaQuery().eq(commodityDetailsImage::getCommodityId, commodityId).list();
        for (commodityDetailsImage commodityDetailsImage : list) {
            try {
                commodityDetailsImage.setImage
                        (imgUtils.encodeImageToBase64
                                (detailsImageAddress+"\\"+commodityDetailsImage.getImage()));
            } catch (IOException e) {
                e.printStackTrace();
                return Result.fail(Code.SYSTEM_ERR,"获取详情图片失败");
            }
        }
        return Result.success(list);
    }
}
