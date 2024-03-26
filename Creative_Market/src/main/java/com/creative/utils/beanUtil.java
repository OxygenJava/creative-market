package com.creative.utils;

import com.creative.domain.commodity;
import com.creative.domain.commodityHomePage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class beanUtil {

    /**
     * 拷贝商品类对象
     * @param shopImage
     * @param commodity
     * @return
     */
    public static commodityHomePage copyCommodity(String shopImage,commodity commodity){

        commodityHomePage commodityHomePage = new commodityHomePage();
        commodityHomePage.setCommodityId(commodity.getId());
        commodityHomePage.setHomePageImage(commodity.getHomePageImage());
        commodityHomePage.setLikesReceived(commodity.getLikesReceived());
        commodityHomePage.setCrowdfundedAmount(commodity.getCrowdfundedAmount());
        commodityHomePage.setState(commodity.getState());
        commodityHomePage.setSupportNumber(commodity.getSupportNumber());

        /**
         * 获取图片高宽
         */
        int height = 0;
        int width = 0;
        try {
            File file = new File(shopImage,commodity.getHomePageImage());
            System.out.println(commodity.getHomePageImage());
                if (file.exists()){
                    BufferedImage read = ImageIO.read(file);
                    height = read.getHeight();
                    width = read.getWidth();
                }
                System.out.println(height);
                 System.out.println(width);

        } catch (IOException e) {
            e.printStackTrace();
        }

        commodityHomePage.setImageWidth(width);
        commodityHomePage.setImageHeight(height);

        return commodityHomePage;
    }
}
