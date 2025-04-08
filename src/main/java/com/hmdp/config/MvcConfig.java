package com.hmdp.config;
/**
 * config: 存放配置类，通常用于定义Spring Bean、配置文件等。
 */

import com.hmdp.utils.LoginInterceptor;
import com.hmdp.utils.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 是一个 Spring Boot 应用中的配置类，主要用于设置 Spring MVC 的拦截器。我们逐步解析其中的关键部分：
 */
@Configuration
/**
 * @Configuration 注解
 * @Configuration：标识该类是一个配置类，Spring 在启动时会自动加载并应用其中的配置。
 */
public class MvcConfig implements WebMvcConfigurer {
    // implements WebMvcConfigurer：实现 WebMvcConfigurer 接口，允许自定义 Spring MVC 的配置，例如添加拦截器、视图解析器等。
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @Autowired：自动注入 Spring 容器中的 StringRedisTemplate 实例，用于在拦截器中操作 Redis 数据。
     * 注入 StringRedisTemplate
     */
// 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addInterceptors 方法：用于注册自定义的拦截器。
        // registry.addInterceptor(...)：向 Spring MVC 的拦截器链中添加新的拦截器。
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).order(0);
        //        3.1. RefreshTokenInterceptor
        //        作用：通过 stringRedisTemplate 与 Redis 交互，可能用于刷新用户的令牌（Token），以保持用户的登录状态。
        //        order(0)：设置该拦截器的执行顺序为 0，数值越小，优先级越高，意味着它会最先执行。
        registry.addInterceptor(new LoginInterceptor()).
                //     3.2. LoginInterceptor
                //     作用：用于拦截需要用户登录的请求，检查用户是否已登录。
                        excludePathPatterns("/user/login",
                        "/user/code",
                        "/blog/hot",
                        "/shop/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/voucher/**").order(1);
        /**
         * excludePathPatterns(...)：配置不需要拦截的路径，即这些路径可以匿名访问。例如：
         * /user/login：用户登录接口
         * /user/code：获取验证码接口
         * /blog/hot：热门博客查看接口
         * /shop/**：商店相关接口
         * /shop-type/**：商店类型相关接口
         * /upload/**：文件上传接口
         * /voucher/**：优惠券相关接口
         * order(1)：设置该拦截器的执行顺序为 1，优先级低于 RefreshTokenInterceptor。
         */
    }
}
