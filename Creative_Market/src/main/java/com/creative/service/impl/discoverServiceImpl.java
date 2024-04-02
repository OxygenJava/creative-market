package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.creative.domain.discovered;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.mapper.DiscoveredMapper;
import com.creative.service.discoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public class discoverServiceImpl extends ServiceImpl<DiscoveredMapper, discovered> implements discoverService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 发布论坛
     * @param file
     * @param disc
     * @param request
     * @return
     */
    @Override
    public Result uploadDiscover(MultipartFile[] file, discovered disc, HttpServletRequest request) {
        //获取请求头信息
        String header = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(header);
        if (entries.isEmpty()){
           return Result.fail(Code.SYNTAX_ERROR,"您尚未登录");
        }
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
        return Result.success(userDTO);
    }
}
