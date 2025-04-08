package com.hmdp.service.impl;

/**
 * 定义了一个名为 BlogServiceImpl 的服务实现类，继承自 MyBatis-Plus 提供的 ServiceImpl 类，
 * 并实现了自定义的 IBlogService 接口。该类主要负责处理与博客（Blog）相关的业务逻辑。
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.dto.ScrollResult;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Follow;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IBlogService;
import com.hmdp.service.IFollowService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.hmdp.utils.RedisConstants.FEED_KEY;

/**
 * 1. 类声明：
 *
 * @Service：Spring 的注解，标识该类为服务层组件，便于 Spring 容器管理和自动装配。
 * extends ServiceImpl<BlogMapper, Blog>：继承 MyBatis-Plus 提供的 ServiceImpl 类，
 * 泛型参数分别为对应的 Mapper 接口（BlogMapper）和实体类（Blog）。
 * implements IBlogService：实现自定义的服务接口 IBlogService，定义了博客相关的业务方法。
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    /**
     * 2. 依赖注入
     * userService：用户服务接口，用于获取用户信息。
     * blogService：博客服务接口，可能用于调用其他博客相关的方法。
     * stringRedisTemplate：Spring 提供的 Redis 操作模板，用于与 Redis 进行交互。
     * followService：关注服务接口，用于处理用户关注相关的操作。
     */
    @Resource
    private IUserService userService;

    @Autowired
    private IBlogService blogService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IFollowService followService;

    /**
     * 3. 主要方法：
     * queryById(Integer id)： 根据博客 ID 查询博客信息，若不存在则返回失败信息
     *
     * @param id
     * @return getById(id)：调用父类方法，根据 ID 获取博客对象。
     * queryBlogUser(blog)：查询并设置博客作者信息。
     * isBlogLiked(blog)：检查当前用户是否已点赞该博客。
     */
    @Override
    public Result queryById(Integer id) {
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("博客不存在或已被删除");
        }
        queryBlogUser(blog);
        //追加判断blog是否被当前用户点赞，逻辑封装到isBlogLiked方法中
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    /**
     * queryHotBlog(Integer current)： 分页查询热门博客，按照点赞数降序排列。
     *
     * @param current
     * @return query()：构建查询条件。
     * orderByDesc("liked")：按照 liked 字段降序排序。
     * page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE))：分页查询。
     */
    @Override
    public Result queryHotBlog(Integer current) {
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            queryBlogUser(blog);
            //追加判断blog是否被当前用户点赞，逻辑封装到isBlogLiked方法中
            isBlogLiked(blog);
        });
        return Result.ok(records);
    }

    /**
     * likeBlog(Long id)： 处理博客点赞和取消点赞的逻辑
     *
     * @param blog UserHolder.getUser().getId()：获取当前登录用户的 ID。
     *             stringRedisTemplate.opsForZSet()：使用 Redis 的有序集合存储和查询点赞信息。
     *             update().setSql("liked = liked + 1").eq("id", id).update()：
     *             使用 MyBatis-Plus 的 update 方法，更新点赞数。
     */
    private void isBlogLiked(Blog blog) {
        //1. 获取当前用户信息
        UserDTO userDTO = UserHolder.getUser();
        //当用户未登录时，就不判断了，直接return结束逻辑
        if (userDTO == null) {
            return;
        }

        //2. 判断当前用户是否点赞
        String key = BLOG_LIKED_KEY + blog.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userDTO.getId().toString());
        blog.setIsLike(score != null);
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    @Override
    public Result likeBlog(Long id) {
        //1. 获取当前用户信息
        Long userId = UserHolder.getUser().getId();
        //2. 如果当前用户未点赞，则点赞数 +1，同时将用户加入set集合
        String key = BLOG_LIKED_KEY + id;
        //尝试获取score
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        //为null，则表示集合中没有该用户
        if (score == null) {
            //点赞数 +1
            boolean success = update().setSql("liked = liked + 1").eq("id", id).update();
            //将用户加入set集合
            if (success) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
            //3. 如果当前用户已点赞，则取消点赞，将用户从set集合中移除
        } else {
            //点赞数 -1
            boolean success = update().setSql("liked = liked - 1").eq("id", id).update();
            if (success) {
                //从set集合移除
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result queryBlogLikes(Integer id) {
        String key = BLOG_LIKED_KEY + id;
        //zrange key 0 4  查询zset中前5个元素
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        //如果是空的(可能没人点赞)，直接返回一个空集合
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        //将ids使用`,`拼接，SQL语句查询出来的结果并不是按照我们期望的方式进行排
        //所以我们需要用order by field来指定排序方式，期望的排序方式就是按照查询出来的id进行排序
        String idsStr = StrUtil.join(",", ids);
        //select * from tb_user where id in (ids[0], ids[1] ...) order by field(id, ids[0], ids[1] ...)
        List<UserDTO> userDTOS = userService.query().in("id", ids)
                .last("order by field(id," + idsStr + ")")
                .list().stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(userDTOS);
    }

    /**
     * saveBlog(Blog blog)： 保存新的博客，并将其推送到关注该用户的粉丝的收件箱中。
     * blog.setUserId(user.getId())：设置博客作者 ID。
     * blogService.save(blog)：保存博客。
     * followService.query().eq("follow_user_id", user.getId()).list()：查询关注当前用户的
     */
    @Override
    public Result saveBlog(Blog blog) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        boolean isSuccess = blogService.save(blog);
        if (!isSuccess) {
            return Result.fail("新增笔记失败！");
        }
        //如果保存成功，则获取保存笔记的发布者id，用该id去follow_user表中查对应的粉丝id
//        select * from tb_follower where follow_user_id = ?
        List<Follow> followUsers = followService.query().eq("follow_user_id", user.getId()).list();
        for (Follow follow : followUsers) {
            Long userId = follow.getUserId();
            String key = FEED_KEY + userId;
            //推送数据,每一个粉丝都有自己的收件箱
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }

        // 返回id
        return Result.ok(blog.getId());
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        //1. 获取当前用户
        Long userId = UserHolder.getUser().getId();
        //2. 查询该用户收件箱（之前我们存的key是固定前缀 + 粉丝id），所以根据当前用户id就可以查询是否有关注的人发了笔记
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typeTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        //3. 非空判断
        if (typeTuples == null || typeTuples.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        //4. 解析数据，blogId、minTime（时间戳）、offset，这里指定创建的list大小，可以略微提高效率，因为我们知道这个list就得是这么大
        ArrayList<Long> ids = new ArrayList<>(typeTuples.size());
        long minTime = 0;
        int os = 1;
        for (ZSetOperations.TypedTuple<String> typeTuple : typeTuples) {
            //4.1 获取id
            String id = typeTuple.getValue();
            ids.add(Long.valueOf(id));
            //4.2 获取score（时间戳）
            long time = typeTuple.getScore().longValue();
            if (time == minTime) {
                os++;
            } else {
                minTime = time;
                os = 1;
            }
        }
        //解决SQL的in不能排序问题，手动指定排序为传入的ids
        String idsStr = StrUtil.join(",");

        //5. 根据id查询blog
        List<Blog> blogs = query().in("id", ids).last("ORDER BY FIELD(id," + idsStr + ")").list();

        for (Blog blog : blogs) {
            //5.1 查询发布该blog的用户信息
            queryBlogUser(blog);
            //5.2 查询当前用户是否给该blog点过赞
            isBlogLiked(blog);
        }
        //6. 封装结果并返回
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setList(blogs);
        scrollResult.setOffset(os);
        scrollResult.setMinTime(minTime);
        return Result.ok(scrollResult);
    }
}
