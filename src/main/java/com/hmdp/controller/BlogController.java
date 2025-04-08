package com.hmdp.controller;
/**
 * BlogController 的 Spring Boot 控制器类，主要用于处理与博客相关的 HTTP 请求。
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.service.IBlogService;
import com.hmdp.service.IFollowService;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */

/**
 * 类级别的注解
 *
 * @RestController： 这是 Spring 4 引入的一个复合注解，
 * 等同于同时使用 @Controller 和 @ResponseBody。
 * 它的作用是将该类标识为一个控制器，并且其方法的返回值会直接作为 HTTP 响应体返回，通常用于构建 RESTful API。
 * @RequestMapping("/blog")： 为该控制器指定基本的请求路径，即所有以 /blog 开头的请求都会由这个控制器处理。
 */
@RestController
@RequestMapping("/blog")
public class BlogController {
    /**
     * 依赖注入
     *
     * @Autowired private IBlogService blogService;：
     * 自动注入 IBlogService 接口的实现，用于处理博客相关的业务逻辑。
     * @Autowired private StringRedisTemplate stringRedisTemplate;
     * 自动注入 StringRedisTemplate，用于与 Redis 进行交互，可能涉及缓存或其他 Redis 操作。
     * @Autowired private IFollowService followService;
     * 自动注入 IFollowService 接口的实现，用于处理关注相关的业务逻辑。
     */
    @Autowired
    private IBlogService blogService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IFollowService followService;

    /**
     * 控制器方法：1. 发布博客
     *
     * @param blog
     * @PostMapping：处理 HTTP POST 请求，对应的路径是 /blog。
     * @RequestBody Blog blog：将请求体中的 JSON 数据反序列化为 Blog 对象。
     * 调用 blogService.saveBlog(blog) 方法保存博客，并返回操作结果。
     */
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        return blogService.saveBlog(blog);
    }

    /**
     * 2. 点赞博客
     *
     * @PutMapping("/like/{id}")：处理对路径 /blog/like/{id} 的 HTTP PUT 请求，其中 {id} 是路径变量，表示博客的 ID。
     * @PathVariable("id") Long id：将路径中的 id 提取为方法参数。
     * 调用 blogService.likeBlog(id) 方法为指定的博客点赞，并返回操作结果。
     */
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    /**
     * 3. 查询博客的点赞数
     *
     * @GetMapping("/likes/{id}")：处理对路径 /blog/likes/{id} 的 HTTP GET 请求。
     * @PathVariable Integer id：将路径中的 id 提取为方法参数。
     * 调用 blogService.queryBlogLikes(id) 方法查询指定博客的点赞数，并返回结果。
     */
    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable Integer id) {
        return blogService.queryBlogLikes(id);
    }

    /**
     * 4. 查询当前用户的博客
     *
     * @GetMapping("/of/me")：处理对路径 /blog/of/me 的 HTTP GET 请求。
     * @RequestParam(value = "current", defaultValue = "1") Integer current：获取查询参数 current，表示当前页码，默认为 1。
     * 通过 UserHolder.getUser() 获取当前登录用户的信息。
     * 使用 blogService.query() 方法根据用户 ID 查询博客，并进行分页处理。
     * 返回当前页的博客列表。
     */
    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    /**
     * 5. 根据用户 ID 查询博客
     *
     * @GetMapping("/of/user")：处理对路径 /blog/of/user 的 HTTP GET 请求。
     * @RequestParam(value = "current", defaultValue = "1") Integer current：获取查询参数 current，表示当前页码，默认为 1。
     * @RequestParam("id") Long id：获取查询参数 id，表示用户 ID。
     * 使用 blogService.query() 方法根据指定用户 ID 查询博客，并进行分页处理。
     * 返回当前页的博客列表。
     */
    @GetMapping("/of/user")
    public Result queryBlogByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    /**
     * 1. 查询关注的博客
     *
     * @GetMapping("/of/follow")：该注解表示该方法处理对路径 /blog/of/follow 的 HTTP GET 请求。
     * @RequestParam("lastId") Long max：从请求参数中获取名为 lastId 的值，并将其赋给方法参数 max。
     * @RequestParam(value = "offset", defaultValue = "0") Integer offset：从请求参数中获取名为 offset 的值，
     * 如果未提供该参数，则使用默认值 0。
     * blogService.queryBlogOfFollow(max, offset)：调用 blogService 的 queryBlogOfFollow 方法，查询关注的博客列表。
     */
    @GetMapping("/of/follow")
    public Result queryBlogOfFollow(@RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return blogService.queryBlogOfFollow(max, offset);
    }

    /**
     * 2. 查询热门博客
     *
     * @GetMapping("/hot")：该注解表示该方法处理对路径 /blog/hot 的 HTTP GET 请求。
     * @RequestParam(value = "current", defaultValue = "1") Integer current：从请求参数中获取名为 current 的值，表示当前页码，如果未提供该参数，则使用默认值 1。
     * blogService.queryHotBlog(current)：调用 blogService 的 queryHotBlog 方法，查询热门博客列表。
     */
    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlog(current);
    }

    /**
     * 3. 根据 ID 查询博客
     *
     * @GetMapping("/{id}")：该注解表示该方法处理对路径 /blog/{id} 的 HTTP GET 请求，其中 {id} 是路径变量。
     * @PathVariable Integer id：从路径中提取变量 id 的值，并将其赋给方法参数 id。
     * blogService.queryById(id)：调用 blogService 的 queryById 方法，根据提供的 id 查询对应的博客。
     */
    @GetMapping("/{id}")
    public Result queryById(@PathVariable Integer id) {
        return blogService.queryById(id);
    }
}
/**
 * 这些方法通过使用 @RequestParam 和 @PathVariable 注解，分别从请求参数和路径变量中获取数据，以处理不同的查询需求。
 */
