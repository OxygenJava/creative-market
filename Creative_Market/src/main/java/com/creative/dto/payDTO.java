package com.creative.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.creative.domain.addressInfo;
import com.creative.domain.buyType;
import com.creative.domain.commodity;
import lombok.Data;

@Data
public class payDTO {

    @TableField(exist = false)
    private addressInfo addressInfo;

    @TableField(exist = false)
    private commodity commodity;

    @TableField(exist = false)
    private buyType buyType;
}
