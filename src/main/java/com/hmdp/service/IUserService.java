package com.hmdp.service;
/**
 * 定义了一个名为 IUserService 的 Java 接口，
 * 继承自 MyBatis-Plus 提供的 IService<User> 接口，并声明了四个与用户操作相关的方法。
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

/**
 * 1. 接口定义
 * IUserService：这是一个用户服务接口，定义了与用户相关的业务操作。
 * extends IService<User>：IService 是 MyBatis-Plus 提供的通用服务接口，
 * 封装了基本的 CRUD（创建、读取、更新、删除）操作。
 * 通过继承 IService<User>，IUserService 接口可以直接使用这些通用方法，无需重复编写代码。
 */
public interface IUserService extends IService<User> {
    /**
     * 2. 方法解析
     * 功能：向指定手机号发送验证码。
     * 参数：
     * phone：接收验证码的手机号。
     * session：当前的 HTTP 会话，用于存储与用户会话相关的信息。
     * 异常：可能抛出 MessagingException，表示在发送消息过程中出现错误。
     * 返回值：Result 对象，通常用于封装操作结果，如成功或失败的信息。
     *
     * @param phone
     * @param session
     * @return
     * @throws MessagingException
     */
    Result sendCode(String phone, HttpSession session) throws MessagingException;

    /**
     * 功能：处理用户登录操作。
     * 参数：
     * loginForm：包含登录信息的数据传输对象（DTO），如用户名和密码。
     * session：当前的 HTTP 会话，用于在会话中存储用户的登录状态或其他相关信息。
     * 返回值：Result 对象，指示登录操作的结果。
     *
     * @param loginForm
     * @param session
     * @return
     */
    Result login(LoginFormDTO loginForm, HttpSession session);

    /**
     * 功能：处理用户签到操作。
     * 参数：无。
     * 返回值：Result 对象，指示签到操作的结果。
     *
     * @return
     */
    Result sign();

    Result signCount();
}
/**
 * 3. 相关技术概念
 * MyBatis-Plus 的 IService 接口：IService 接口提供了通用的 CRUD 操作，简化了对数据库的基本操作。
 * 通过继承 IService<User>，可以直接使用这些方法，如 save、removeById、updateById 和 getById 等。
 * Spring 的 HttpSession：HttpSession 是 Java EE 提供的接口，用于在服务器端存储用户会话信息。
 * Spring 框架对 HttpSession 提供了支持，允许开发者在应用中轻松管理用户会话数据。
 */