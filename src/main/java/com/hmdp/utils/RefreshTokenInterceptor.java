package com.hmdp.utils;
/**
 * RefreshTokenInterceptor 是一个实现了 HandlerInterceptor 接口的拦截器，
 * 主要用于在每次 HTTP 请求时，检查并刷新用户的登录状态，
 * 确保用户在活跃期间不会因 Token 过期而被强制登出。
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenInterceptor implements HandlerInterceptor {
    // 这里并不是自动装配，因为RefreshTokenInterceptor是我们手动在WebConfig里new出来的
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 该构造函数接收一个 StringRedisTemplate 对象，用于与 Redis 进行交互。
     * 由于 RefreshTokenInterceptor 是在 WebConfig 中手动实例化的，
     * s因此需要通过构造函数注入 StringRedisTemplate。
     *
     * @param stringRedisTemplate
     */
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求头中的token
        String token = request.getHeader("authorization");
        // 2. 如果token是空，直接放行，交给LoginInterceptor处理
        if (StrUtil.isBlank(token)) {
            return true;
        }
        String key = RedisConstants.LOGIN_USER_KEY + token;
        // 3. 基于token获取Redis中的用户数据
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        // 4. 判断用户是否存在，不存在，也放行，交给LoginInterceptor
        if (userMap.isEmpty()) {
            return true;
        }
        // 5. 将查询到的Hash数据转化为UserDto对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 6. 将用户信息保存到ThreadLocal
        UserHolder.saveUser(userDTO);
        // 7. 刷新tokenTTL，这里的存活时间根据需要自己设置，这里的常量值我改为了30分钟
        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除threadlocal用户，避免内存泄漏
        UserHolder.removeUser();
    }
}
