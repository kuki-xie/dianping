package com.hmdp.utils;
/**
 * 定义了一个名为 LoginInterceptor 的类，实现了 HandlerInterceptor 接口。
 * 它的主要作用是在处理 HTTP 请求之前，对用户的登录状态进行检查。
 */

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类定义：
 * LoginInterceptor 类实现了 HandlerInterceptor 接口，
 * 这是 Spring MVC 提供的拦截器接口，允许在请求处理的不同阶段插入自定义逻辑。
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * preHandle 方法：
     * 这是 HandlerInterceptor 接口中的方法，在请求到达控制器（Controller）之前执行。
     * 它的返回值决定了请求是否继续执行：
     * 返回 true：请求将继续传递到下一个拦截器或最终的处理器（Controller）。
     * 返回 false：请求被中断，不再继续传递。
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        1.判断是否需要拦截
        /*
        用户身份检查：
        UserHolder.getUser()：假设这是一个自定义的工具类方法，用于获取当前线程关联的用户信息。
        如果返回 null，表示用户未登录。
        如果用户未登录，设置响应状态码为 401（未授权），并返回 false，中断请求处理流程。
         */
        // UserHolder的所有方法都是static的
        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除threadlocal用户，避免内存泄漏
        UserHolder.removeUser();
    }
}
