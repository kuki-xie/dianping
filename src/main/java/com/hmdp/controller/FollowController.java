package com.hmdp.controller;
/**
 * 名为 FollowController 的 Spring Boot 控制器类，主要用于处理与用户关注相关的 HTTP 请求。
 */

import com.hmdp.dto.Result;
import com.hmdp.service.IFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 类级别的注解
 *
 * @RestController： 这是一个组合注解，等同于同时使用 @Controller 和 @ResponseBody。
 * 它的作用是将该类标识为一个控制器，并且其方法的返回值会直接作为 HTTP 响应体返回，通常用于构建 RESTful API。
 * @RequestMapping("/follow")：为该控制器指定基本的请求路径，即所有以 /follow 开头的请求都会由这个控制器处理。
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    /**
     * 依赖注入
     *
     * @Resource private IFollowService followService;：
     * 通过 @Resource 注解自动注入 IFollowService 接口的实现，用于处理关注相关的业务逻辑。
     */
    @Resource
    private IFollowService followService;

    /**
     * 控制器方法
     * 判断当前用户是否关注了指定用户
     *
     * @GetMapping("/or/not/{id}")：处理对路径 /follow/or/not/{id} 的 HTTP GET 请求，
     * 其中 {id} 是路径变量，表示被关注用户的 ID。
     * @PathVariable("id") Long followUserId：将路径中的 id 提取为方法参数 followUserId。
     * followService.isFollow(followUserId)：调用 followService 的 isFollow 方法，
     * 判断当前用户是否关注了指定的用户，并返回结果。
     */
    //判断当前用户是否关注了该博主,加载页面的时候就会发起请求
    @GetMapping("/or/not/{id}")
    public Result isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    /**
     * 2. 实现关注或取关操作
     *
     * @PutMapping("/{id}/{isFollow}")：处理对路径 /follow/{id}/{isFollow} 的 HTTP PUT 请求，
     * 其中 {id} 是被关注用户的 ID，{isFollow} 是布尔值，表示执行关注（true）或取关（false）操作。
     * @PathVariable("id") Long followUserId：将路径中的 id 提取为方法参数 followUserId。
     * @PathVariable("isFollow") Boolean isFellow：将路径中的 isFollow 提取为方法参数 isFellow，表示是否关注。
     * followService.follow(followUserId, isFellow)：调用 followService 的 follow 方法，
     * 执行关注或取关操作，并返回结果。
     */
    //实现取关/关注
    @PutMapping("/{id}/{isFollow}")
    public Result follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFellow) {
        return followService.follow(followUserId, isFellow);
    }

    /**
     * 3. 查询共同关注的用户
     *
     * @GetMapping("/common/{id}")：处理对路径 /follow/common/{id} 的 HTTP GET 请求，
     * 其中 {id} 是路径变量，表示其他用户的 ID。
     * @PathVariable Long id：将路径中的 id 提取为方法参数。
     * followService.followCommons(id)：调用 followService 的 followCommons 方法，
     * 查询当前用户与指定用户的共同关注者，并返回结果。
     */
    //    共同关注代码
    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable Long id) {
        return followService.followCommons(id);
    }
}
