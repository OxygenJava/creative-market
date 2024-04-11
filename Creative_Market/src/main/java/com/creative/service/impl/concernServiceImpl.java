package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creative.domain.concern;
import com.creative.domain.user;
import com.creative.dto.Code;
import com.creative.dto.Result;
import com.creative.dto.UserDTO;
import com.creative.dto.fansDTO;
import com.creative.mapper.concernMapper;
import com.creative.mapper.userMapper;
import com.creative.service.concernService;
import com.creative.utils.imgUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class concernServiceImpl implements concernService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private concernMapper concernMapper;

    @Autowired
    private userMapper userMapper;

    @Value("${creativeMarket.iconImage}")
    private String iconImage;

    //关注
    @Override
    public Result concernPerson(concern concern, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        if (entries.isEmpty()) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        }
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if (user.getId().equals(concern.getConcernId())) {
            return new Result(Code.SYNTAX_ERROR, "请不要自我关注", "");
        }


        LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
        lqw.eq(com.creative.domain.concern::getUid, user.getId())
                .eq(com.creative.domain.concern::getConcernId, concern.getConcernId());
        concern concern1 = concernMapper.selectOne(lqw);

        //排除多次关注同一个人
        if (concern1 != null) {
            return new Result(Code.SYNTAX_ERROR, "您已经关注过他了", "");
        }
        concern.setUid(user.getId());
        LocalDateTime dateTime = LocalDateTime.now();
        concern.setConcernTime(dateTime);
        int insert = concernMapper.insert(concern);

        //用户关注数加一
        com.creative.domain.user user1 = userMapper.selectById(user.getId());
        user1.setFocusCount(user1.getFocusCount() + 1);
        int update1 = userMapper.updateById(user1);

        //用户粉丝数加一
        com.creative.domain.user user2 = userMapper.selectById(concern.getConcernId());
        user2.setFansCount(user2.getFansCount() + 1);
        int update2 = userMapper.updateById(user2);

        Integer code = insert > 0 && update1 > 0 && update2 > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = insert > 0 && update1 > 0 && update2 > 0 ? "关注成功" : "关注失败";
        return new Result(code, msg, "");

    }

    //取消关注
    @Override
    public Result cancelConcern(concern concern, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        if (entries.isEmpty()) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        }
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        user user2 = userMapper.selectById(concern.getConcernId());
        if (user2 == null) {
            return new Result(Code.SYNTAX_ERROR, "没有该用户", "");
        }

        if (user.getId().equals(concern.getConcernId())) {
            return new Result(Code.SYNTAX_ERROR, "请不要自我取关", "");
        }

        //判断用户是否有关注
        LambdaQueryWrapper<concern> concernLqw = new LambdaQueryWrapper<>();
        concernLqw.eq(com.creative.domain.concern::getUid, user.getId())
                .eq(com.creative.domain.concern::getConcernId, concern.getConcernId());
        concern concern1 = concernMapper.selectOne(concernLqw);
        if (concern1 == null) {
            return Result.fail(Code.SYNTAX_ERROR, "您还没有关注过他，无法取关");
        }

        //删除收藏表
        LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
        lqw.eq(com.creative.domain.concern::getUid, user.getId())
                .eq(com.creative.domain.concern::getConcernId, concern.getConcernId());
        int delete = concernMapper.delete(lqw);

        //用户1添加收藏
        user user1 = userMapper.selectById(user.getId());
        user1.setFocusCount(user1.getFocusCount() - 1);
        int update1 = userMapper.updateById(user1);

//        用户2添加粉丝
        user2.setFansCount(user2.getFansCount() - 1);
        int update2 = userMapper.updateById(user2);

        Integer code = delete > 0 && update1 > 0 && update2 > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete > 0 && update1 > 0 && update2 > 0 ? "取消关注成功" : "取消关注失败";
        return new Result(code, msg, "");
    }


    //判断是否关注了
    @Override
    public Result ifconcern(Integer uid, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        com.creative.domain.user user1 = userMapper.selectById(uid);

        if (user1 == null) {
            return new Result(Code.SYNTAX_ERROR, "该用户不存在", "");
        } else if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else if (user.getId() == uid) {
            return new Result(Code.SYNTAX_ERROR, "请不要自我关注", "");
        } else {
            LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
            lqw.eq(com.creative.domain.concern::getUid, user.getId())
                    .eq(com.creative.domain.concern::getConcernId, uid);
            concern concern = concernMapper.selectOne(lqw);

            if (concern == null) {
                return new Result(Code.SYNTAX_ERROR, "", 0);
            } else {
                return new Result(Code.NORMAL, "", 1);
            }
        }
    }

    //获取粉丝名单
    @Override
    public Result ObtainFans(Integer pageSize, Integer pageNumber, HttpServletRequest request) {
        ArrayList list = new ArrayList();
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else {
            IPage page = new Page(pageNumber, pageSize);
            LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
            lqw.eq(concern::getConcernId, user.getId());
            concernMapper.selectPage(page, lqw);
            List<concern> concerns = page.getRecords();
            for (concern concern : concerns) {
                Integer uid = concern.getUid();
                user user1 = userMapper.selectById(uid);
                fansDTO fansDTO = BeanUtil.copyProperties(user1, fansDTO.class);
                try {
                    fansDTO.setIconImage(imgUtils.encodeImageToBase64(iconImage + "\\" + fansDTO.getIconImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //查询我的关注内是否有他
                LambdaQueryWrapper<concern> conLqw = new LambdaQueryWrapper<>();
                conLqw.eq(com.creative.domain.concern::getConcernId, fansDTO.getId())
                        .eq(com.creative.domain.concern::getUid, concern.getConcernId());
                com.creative.domain.concern concern1 = concernMapper.selectOne(conLqw);
                if (concern1 != null) {
                    fansDTO.setIsConcern(1);
                } else {
                    fansDTO.setIsConcern(0);
                }
                list.add(fansDTO);
            }

            if (page.getRecords().size() <= 0) {
                return new Result(Code.SYNTAX_ERROR, "数据已经到底", "");
            }

            Collections.reverse(list);
            Integer code = concerns.size() != 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = concerns.size() != 0 ? "查询粉丝成功" : "您还没有粉丝";
            return new Result(code, msg, list);
        }

    }


    //获取关注名单
    @Override
    public Result ObtainFocus(Integer pageSize, Integer pageNumber, HttpServletRequest request) {

        ArrayList list = new ArrayList();
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else {
            IPage page = new Page(pageNumber, pageSize);
            LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
            lqw.eq(concern::getUid, user.getId());
            concernMapper.selectPage(page, lqw);
            List<concern> concerns = page.getRecords();
            for (concern concern : concerns) {
                Integer concernId = concern.getConcernId();
                com.creative.domain.user user1 = userMapper.selectById(concernId);
                UserDTO userDTO = BeanUtil.copyProperties(user1, UserDTO.class);
                try {
                    userDTO.setIconImage(imgUtils.encodeImageToBase64(iconImage + "\\" + userDTO.getIconImage()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(userDTO);
            }

            if (page.getRecords().size() <= 0) {
                return new Result(Code.SYNTAX_ERROR, "数据已经到底", "");
            }
            Collections.reverse(list);
            Integer code = concerns.size() != 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = concerns.size() != 0 ? "查询关注成功" : "您还没有关注";
            return new Result(code, msg, list);
        }
    }


    //关注用户的模糊查询
    @Override
    public Result selectLikeFocus(String name, HttpServletRequest request) {

        List<UserDTO> userDTOs = new ArrayList<>();
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else {
            LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
            lqw.eq(concern::getUid, user.getId());
            List<concern> concerns = concernMapper.selectList(lqw);
            if (concerns.size() == 0) {
                return new Result(Code.SYNTAX_ERROR, "您还没有关注", "");
            } else {
                for (concern concern : concerns) {
                    LambdaQueryWrapper<user> lqw1 = new LambdaQueryWrapper<>();
                    lqw1.eq(com.creative.domain.user::getId, concern.getConcernId())
                            .like(com.creative.domain.user::getUsername, name)
                            .or()
                            .eq(com.creative.domain.user::getId, concern.getConcernId())
                            .like(com.creative.domain.user::getNickName, name);
                    List<com.creative.domain.user> users = userMapper.selectList(lqw1);
                    List<UserDTO> userDTOS = BeanUtil.copyToList(users, UserDTO.class);
                    userDTOs.addAll(userDTOS);
                }

                Integer code = userDTOs.size() != 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
                String msg = userDTOs.size() != 0 ? "查询关注成功" : "查询不到该用户";
                return new Result(code, msg, userDTOs);
            }

        }


    }

    //粉丝用户的模糊查询
    @Override
    public Result selectLikeFans(String name, HttpServletRequest request) {

        List<UserDTO> userDTOs = new ArrayList<>();
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);

        if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else {
            LambdaQueryWrapper<concern> lqw = new LambdaQueryWrapper<>();
            lqw.eq(concern::getConcernId, user.getId());
            List<concern> concerns = concernMapper.selectList(lqw);
            if (concerns.size() == 0) {
                return new Result(Code.SYNTAX_ERROR, "您还没有关注", "");
            } else {
                for (concern concern : concerns) {
                    LambdaQueryWrapper<user> lqw1 = new LambdaQueryWrapper<>();
                    lqw1.eq(com.creative.domain.user::getId, concern.getUid())
                            .like(com.creative.domain.user::getUsername, name)
                            .or()
                            .eq(com.creative.domain.user::getId, concern.getUid())
                            .like(com.creative.domain.user::getNickName, name);
                    List<com.creative.domain.user> users = userMapper.selectList(lqw1);
                    List<UserDTO> userDTOS = BeanUtil.copyToList(users, UserDTO.class);
                    userDTOs.addAll(userDTOS);
                }

                Integer code = userDTOs.size() != 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
                String msg = userDTOs.size() != 0 ? "查询关注成功" : "查询不到该用户";
                return new Result(code, msg, userDTOs);
            }

        }
    }

    @Override
    public Result selectFansTotal(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else {
            com.creative.domain.user user1 = userMapper.selectById(user.getId());
            Integer code = user1 != null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = user1 != null ? "查询成功" : "查询失败";
            return new Result(code, msg, user1.getFansCount());
        }
    }

    @Override
    public Result selectFocusTotal(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if (user.getId() == null) {
            return new Result(Code.INSUFFICIENT_PERMISSIONS, "请先登录", "");
        } else {
            com.creative.domain.user user1 = userMapper.selectById(user.getId());
            Integer code = user1 != null ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = user1 != null ? "查询成功" : "查询失败";
            return new Result(code, msg, user1.getFocusCount());
        }

    }

}
