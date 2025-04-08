package com.hmdp.controller;
/**
 * 名为 UserController 的 Spring Boot 控制器，主要负责处理与用户相关的操作，
 * 如发送验证码、登录、登出、获取用户信息和签到等功能。
 */

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.entity.UserInfo;
import com.hmdp.service.IUserInfoService;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

/**
 * 类级别注解
 *
 * @Slf4j：来自 Lombok 库，自动生成日志记录器，方便在类中使用 log 对象进行日志记录。
 * @RestController：标识该类为 RESTful 控制器，返回的结果直接作为 HTTP 响应体。
 * @RequestMapping("/user")：为该控制器指定基础请求路径，即所有以 /user 开头的请求都会由此控制器处理。
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 依赖注入
     *
     * @Resource：用于自动注入依赖对象。这里将 IUserService 和 IUserInfoService 的实现
     * 注入到 userService 和 userInfoService 字段中，分别处理用户和用户信息相关的业务逻辑。
     */
    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    /**
     * 控制器方法解析
     * 1. 发送手机验证码
     *
     * @PostMapping("code")：处理 /user/code 的 POST 请求，用于发送手机验证码。
     * sendCode 方法：
     * 参数：phone 接收前端传来的手机号，session 用于存储验证码信息。
     * 调用 userService 的 sendCode 方法，生成并发送验证码，同时将验证码存储在会话中以供后续验证。
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) throws MessagingException {
        return userService.sendCode(phone, session);
    }

    /**
     * 2. 登录功能
     *
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     * @PostMapping("/login")：处理 /user/login 的 POST 请求，用于用户登录。
     * login 方法：
     * 参数：loginForm 包含登录所需的手机号和验证码或密码，session 用于存储登录状态。
     * 调用 userService 的 login 方法，验证用户信息，成功则在会话中存储用户信息，返回登录成功结果。
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        // 实现登录功能
        return userService.login(loginForm, session);
    }

    /**
     * 3. 登出功能
     *
     * @return 无
     * @PostMapping("/logout")：处理 /user/logout 的 POST 请求，用于用户登出。
     * logout 方法：
     * 当前方法未实现具体的登出逻辑，返回功能未完成的提示。
     */
    @PostMapping("/logout")
    public Result logout() {
        // TODO 实现登出功能
        return Result.fail("功能未完成");
    }

    /**
     * 4. 获取当前登录用户信息
     *
     * @return
     * @GetMapping("/me")：处理 /user/me 的 GET 请求，获取当前登录用户的信息。
     * me 方法：
     * 调用 UserHolder.getUser() 获取当前会话中的用户信息，封装在 Result 对象中返回。
     */
    @GetMapping("/me")
    public Result me() {
        //  获取当前登录的用户并返回
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    /**
     * 5.  获取指定用户详细信息
     *
     * @param userId
     * @return
     * @GetMapping("/info/{id}")：处理 /user/info/{id} 的 GET 请求，获取指定用户的详细信息。
     * info 方法：
     * 参数：userId 指定用户的 ID。
     * 调用 userInfoService.getById(userId) 获取用户详细信息，若不存在则返回空结果。
     * 将 createTime 和 updateTime 字段置空，避免泄露敏感信息。
     * 返回用户详细信息。
     */
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId) {
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        // 返回
        return Result.ok(info);
    }

    /**
     * 6. 根据用户 ID 查询用户信息
     *
     * @param userId
     * @return
     * @GetMapping("/{id}")：处理 /user/{id} 的 GET 请求，查询指定用户的基本信息。
     * queryById 方法：
     * 参数：userId 指定用户的 ID。
     * 调用 userService.getById(userId) 获取用户信息，若不存在则返回空结果。
     * 使用 BeanUtil.copyProperties 将 User 对象转换为 UserDTO，以隐藏敏感信息。
     * 返回用户基本信息。
     */
    @GetMapping("/{id}")
    public Result queryById(@PathVariable("id") Long userId) {
        // 查询详情
        User user = userService.getById(userId);
        if (user == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        // 返回
        return Result.ok(userDTO);
    }

    /**
     * 7. 签到功能
     *
     * @return
     * @PostMapping("/sign")：处理 /user/sign 的 POST 请求，用于用户签到。
     * sign 方法：
     * 调用 userService.sign() 执行签到逻辑，返回签到结果。
     */
    @PostMapping("/sign")
    public Result sign() {
        return userService.sign();
    }

    /**
     * 8. 统计每月签到次数
     *
     * @return
     * @GetMapping("/sign/count")：处理 /user/sign/count 的 GET 请求，统计用户每月的签到次数。
     * signCount 方法：
     * 调用 userService.signCount() 获取当前用户本月的签到次数，返回统计结果。
     */
    @GetMapping("/sign/count")
    public Result signCount() {
        return userService.signCount();
    }
}
/**
 * UserController 提供了与用户相关的多个接口，包括发送验证码、登录、登出、获取用户信息和签到等功能。
 * 通过依赖注入的方式，将具体的业务逻辑委托给 userService 和 userInfoService 实现，
 * 控制器主要负责处理 HTTP 请求和响应。
 */
