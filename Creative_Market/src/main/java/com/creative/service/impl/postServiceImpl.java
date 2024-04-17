package com.creative.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.creative.domain.*;
import com.creative.dto.*;
import com.creative.mapper.*;
import com.creative.service.LableService;
import com.creative.service.postService;
import com.creative.service.userService;
import com.creative.utils.imgUtils;
import com.creative.utils.userHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class postServiceImpl implements postService {

    @Autowired
    private postMapper postMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private userService userService;
    @Value("${creativeMarket.discoverImage}")
    private String discoverImage;
    @Value("${creativeMarket.iconImage}")
    private String iconImage;

    @Autowired
    private LableService lableService;

    @Autowired
    private likepostMapper likepostMapper;
    @Autowired
    private collectionpostMapper collectionpostMapper;

    @Autowired
    private com.creative.mapper.userMapper userMapper;




    /**
     * 上传
     *
     * @param file
     * @param post
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public Result uploadDiscover(MultipartFile[] file, post post, HttpServletRequest request) throws IOException {
        //登录校验
        UserDTO userDTO = getUserDTO(request);
        if (userDTO == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        if (post.getTitle() == null || "".equals(post.getTitle())){
            return Result.fail(Code.SYNTAX_ERROR,"您暂未设置标题");
        }
        if (post.getBody() == null || "".equals(post.getBody())){
            return Result.fail(Code.SYNTAX_ERROR,"您暂未设置正文");
        }
        if (file == null || file.length <= 0){
            return Result.fail(Code.SYNTAX_ERROR,"上传的图片不能为空");
        }
        //设置用户id
        post.setUid(userDTO.getId());
        //设置发布时间
        post.setCreateTime(new Date().getTime());

        if (post.getLableId() == null || "".equals(post.getLableId())){
            return Result.fail(Code.SYNTAX_ERROR,"您暂未设置标签");
        }
        //处理标签
        //根据label获取到响应的labelId，如果没有，则创建
        String[] labels = post.getLableId().split(",");
        StringBuilder labelSb  = new StringBuilder();
        for (String label : labels) {
            lable one = lableService.lambdaQuery().eq(lable::getName, label).one();
            //通过name查询到label存在，则获取id，并拼接
            if (one != null){
                labelSb.append(one.getId()+",");
            }else {
                //此时,用户插入的标签在标签库中不存在
                lable l = new lable();
                l.setName(label);
                l.setCreateTime(LocalDateTime.now());
                lableService.save(l);
                labelSb.append(l.getId()+",");
            }
        }
        post.setLableId(labelSb.toString());

        //用于记录图片字符串，使用逗号拼接
        String image = "";
        //处理图片
        for (MultipartFile multipartFile : file) {
            String originalFilename = multipartFile.getOriginalFilename();
            //获取图片后缀
            String imageLastName = originalFilename.substring(originalFilename.lastIndexOf("."));
            //校验图片的格式
            if (!imageLastName.equals(".jpg") && !imageLastName.equals(".png")){
                return Result.fail(Code.SYNTAX_ERROR,"图片格式必须为 jgp 或 png 格式");
            }
            File discoverImageFile = new File(discoverImage);
            System.out.println(discoverImageFile);
            if (!discoverImageFile.exists()){
                discoverImageFile.mkdirs();
            }

            //生成图片名字
            String imageName = UUID.randomUUID().toString();
            //拼接图片字符串
            image += imageName+imageLastName+",";
            //下载图片
            multipartFile.transferTo(new File(discoverImageFile,imageName+imageLastName));
        }
        //将图片字符串设置在对象中
        post.setImage(image);
        //将该对象存库
        postMapper.insert(post);
        System.out.println(post);
        return Result.success();
    }

    /**
     * 分页获取发现中内容
     *
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @Override
    public Result getAllDiscover(int pageSize, int pageNumber,HttpServletRequest request) throws IOException {
        Page<post> postPage = new Page<>(pageNumber,pageSize);
        Page<post> page = postMapper.selectPage(postPage,null);
        List<post> records = page.getRecords();
        if (records.size() <= 0){
            return Result.success("数据已经到底啦~");
        }
        List<getAllDiscoverDTO> list = new ArrayList<>();

        UserDTO userDTO = userHolder.getUser();

        //判断是否登录
        boolean isLogin = userDTO != null;
        Integer userId = userDTO == null ? null : userDTO.getId();

        for (post record : records) {
            getAllDiscoverDTO getAllDiscoverDTO = BeanUtil.copyProperties(record, getAllDiscoverDTO.class);
            //按照用户是否点赞或收藏设置状态
            if (isLogin){
                Integer postId = record.getId();
                LambdaQueryWrapper<likepost> likeLqw = new LambdaQueryWrapper<>();
                likeLqw.eq(likepost::getPid,postId).eq(likepost::getUid,userId);
                likepost likepost = likepostMapper.selectOne(likeLqw);
                //判断用户是否收藏，收藏状态码为：1
                Integer isLike = likepost == null ? 0 : 1;
                getAllDiscoverDTO.setLikesState(isLike);

                LambdaQueryWrapper<collectionpost> collectionpostLqw = new LambdaQueryWrapper<>();
                collectionpostLqw.eq(collectionpost::getPid,postId).eq(collectionpost::getUid,userId);
                collectionpost collectionpost = collectionpostMapper.selectOne(collectionpostLqw);
                Integer isCollection = collectionpost == null ? 0 : 1;
                getAllDiscoverDTO.setCollectionState(isCollection);
            }else {
                //表示没有登录，此时默认为未点赞
                getAllDiscoverDTO.setCollectionState(0);
                getAllDiscoverDTO.setLikesState(0);
            }

            //根据用户id查询用户
            user one = userService.lambdaQuery().eq(user::getId, record.getUid()).one();
            getAllDiscoverDTO.setUserName(one.getNickName());

            //识别发布时间
            Long releasedTime = record.getCreateTime();
            //获取发布时间与当前时间之差
            String timeStr = getTimeStr(releasedTime);
            getAllDiscoverDTO.setReleasedTime(timeStr);
            //设置用户头像
            if (one.getIconImage() != null){
                String s = imgUtils.encodeImageToBase64(iconImage+"\\"+one.getIconImage());
                getAllDiscoverDTO.setIconImage(s);
            }
            //设置标签
            String[] split = record.getLableId().split(",");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                lable labelOne = lableService.lambdaQuery().eq(lable::getId, s).one();
                sb.append(labelOne.getName()+",");
            }
            getAllDiscoverDTO.setLabel(sb.toString());

            //处理图片
            String[] image = getAllDiscoverDTO.getImage();
            List<String> imageList = new ArrayList<>();
            for (String s : image) {
                if (!"".equals(s)){
                    String s1 = imgUtils.encodeImageToBase64(discoverImage + "\\" + s);
                    imageList.add(s1);
                }
            }
            String[] arrayStr = new String[imageList.size()];
            getAllDiscoverDTO.setImage(imageList.toArray(arrayStr));
            list.add(getAllDiscoverDTO);
        }
        return Result.success(list);
    }



    @Override
    public Result deletePost(Integer id) {
        int delete = postMapper.deleteById(id);
        Integer code = delete > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = delete > 0 ? "删除成功" : "删除失败";
        return new Result(code, msg, "");
    }

    @Override
    public Result updatePost(post post) {
            int update = postMapper.updateById(post);
            Integer code = update > 0 ? Code.NORMAL : Code.SYNTAX_ERROR;
            String msg = update > 0 ? "修改成功" : "修改失败";
            return new Result(code, msg, "");
        }


    @Override
    public Result selectPostAll() {
        List<post> posts = postMapper.selectList(null);
        Integer code = posts != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = posts != null ? "查询成功" : "查询失败";
        return new Result(code, msg, posts);
    }

    @Override
    public Result selectByUidAllPost(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        user user = BeanUtil.fillBeanWithMap(entries, new user(), true);
        if(user==null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }
        LambdaQueryWrapper<post> lqw=new LambdaQueryWrapper<>();
        lqw.eq(post::getUid,user.getId());
        List<post> posts = postMapper.selectList(lqw);
        Integer code = posts != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = posts != null ? "查询成功" : "查询失败";
        return new Result(code, msg, posts);
    }

    /**
     * 获取帖子详情
     *
     * @param postId
     * @return
     */
    @Override
    public Result getPostDetail(Integer postId) {

        UserDTO user = userHolder.getUser();
        if (user == null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }

        post post = postMapper.selectById(postId);
        if (post == null){
            return Result.fail(Code.SYNTAX_ERROR,"该帖子不存在");
        }

        postDTO postDTO = BeanUtil.copyProperties(post, postDTO.class);
        List<String> lableId = postDTO.getLableId();
        //处理标签
        for (int i = 0; i < lableId.size(); i++) {
            lableId.set(i,lableService.getById(lableId.get(i)).getName());
        }
        postDTO.setLableId(lableId);

        //处理用户名
        //获取发布用户
        user getUserById = userService.getById(post.getUid());
        postDTO.setPostUserNickName(getUserById.getNickName());

        //处理相差时间
        Long createTime = post.getCreateTime();
        //获取发布时间与当前时间之差
        String timeStr = getTimeStr(createTime);
        postDTO.setCreateTime(timeStr);

        //处理头像
        try {
            postDTO.setIconImage(imgUtils.encodeImageToBase64(iconImage+"\\"+getUserById.getIconImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //处理照片
        //将图片转换为base64格式
        List<String> image = postDTO.getImage();
        for (int i = 0; i < image.size(); i++) {
            try {
                image.set(i,imgUtils.encodeImageToBase64(discoverImage + "\\" + image.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        postDTO.setImage(image);

        //根据用户id和postId查询点赞表，获取点赞状态
        LambdaQueryWrapper<likepost> likePostLqw = new LambdaQueryWrapper<>();
        likePostLqw.eq(likepost::getUid,user.getId()).eq(likepost::getPid,postId);
        likepost likepost = likepostMapper.selectOne(likePostLqw);
        if (likepost == null){
            //表示该用户没有对该帖子点赞
            //将点赞状态设为0
            postDTO.setLikesState(0);
        }else {
            //表示该用户对该帖子点了赞
            //将点赞状态设为1
            postDTO.setLikesState(1);
        }

        //根据用户id和postId查询点赞收藏表，获取收藏状态
        LambdaQueryWrapper<collectionpost> collectionpostLqw = new LambdaQueryWrapper<>();
        collectionpostLqw.eq(collectionpost::getUid,user.getId()).eq(collectionpost::getPid,postId);
        collectionpost collectionpost = collectionpostMapper.selectOne(collectionpostLqw);
        if (collectionpost == null){
            postDTO.setCollectionState(0);
        }else {
            postDTO.setCollectionState(1);
        }

        return Result.success(postDTO);
    }

    //分页模糊查询所有帖子
    @Override
    public Result selectLikePost(Integer pageSize, Integer pageNumber, post post,HttpServletRequest request) {
        ArrayList<String> Image=new ArrayList<>();
        String authorization = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(authorization);
        UserDTO userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);

        if(userDTO.getId()==null){
            return Result.fail(Code.INSUFFICIENT_PERMISSIONS,"您尚未登录");
        }

        IPage page=new Page(pageNumber,pageSize);
        LambdaQueryWrapper<post> lqw=new LambdaQueryWrapper<>();
        lqw.like(com.creative.domain.post::getBody,post.getBody()).or().like(com.creative.domain.post::getTitle,post.getBody());
        postMapper.selectPage(page,lqw);
        List<post> records = page.getRecords();
        if (records.size() <= 0){
            return Result.success("数据已经到底了");
        }
        List<postDTO> postDTOS = BeanUtil.copyToList(records, postDTO.class);

        LambdaQueryWrapper<likepost> lqw1=new LambdaQueryWrapper<>();
        LambdaQueryWrapper<collectionpost> lqw2=new LambdaQueryWrapper<>();

        for (postDTO postDTO : postDTOS) {
            user user = userMapper.selectById(postDTO.getUid());
            if(user==null){
                return Result.fail(Code.SYNTAX_ERROR,"该用户不存在");
            }
            postDTO.setPostUserNickName(user.getNickName());
            try {
                postDTO.setIconImage(imgUtils.encodeImageToBase64(iconImage+"\\"+user.getIconImage()));
                List<String> image = postDTO.getImage();
                for (String s : image) {
                    String s1 = imgUtils.encodeImageToBase64(discoverImage + "\\" + s);
                    Image.add(s1);
                }
                postDTO.setImage(Image);
                Image.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

            lqw1.eq(likepost::getPid,postDTO.getId())
                    .eq(likepost::getUid,userDTO.getId());
            likepost likepost = likepostMapper.selectOne(lqw1);
            if(likepost!=null){
                postDTO.setLikesState(1);
            }
            else {
                postDTO.setLikesState(0);
            }

            lqw2.eq(collectionpost::getPid,postDTO.getId())
                    .eq(collectionpost::getUid,userDTO.getId());
            collectionpost collectionpost = collectionpostMapper.selectOne(lqw2);
            if(collectionpost!=null){
                postDTO.setCollectionState(1);
            }
            else {
                postDTO.setCollectionState(0);
            }

        }

        Integer code = postDTOS != null ? Code.NORMAL : Code.SYNTAX_ERROR;
        String msg = postDTOS != null ? "查询成功" : "查询失败";
        return new Result(code, msg, postDTOS);

    }


    /**
     * 登录校验
     * @param request
     * @return
     */
    public UserDTO getUserDTO(HttpServletRequest request){
        //获取请求头信息
        String header = request.getHeader("Authorization");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(header);
        if (entries.isEmpty()){
            return null;
        }
        //获取userDTO对象
        return BeanUtil.fillBeanWithMap(entries, new UserDTO(), true);
    }

    /**
     * 获取发布时间与当前时间之差
     * @param releasedTime
     * @return
     */
    private String getTimeStr(Long releasedTime) {
        //获取当前时间值
        long nowTime = new Date().getTime();
        String timeStr = "";
        //当前时间 - 发布时间 = 相差时间
        long l = nowTime - releasedTime;
        //以分钟为单位
        long time = l / 1000 / 60;
        //当计算出来的时间值小于1时
        if (time == 0){
            timeStr = 1+"分钟前";
        }else if (time / 60 / 24 > 0){
            //当相差的时间/60 / 24大于0，表示可以使用天为单位
            timeStr = time / 60 / 24 + "天前";
        }else if (time / 60 > 0){
            //当相差的时间/60大于0，表示可以使用小时为单位
            timeStr = time / 60+"小时前";
        }else {
            timeStr = time + "分钟前";
        }
        return timeStr;
    }

}
