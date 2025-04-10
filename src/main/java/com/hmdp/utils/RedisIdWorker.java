package com.hmdp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 通过 Redis 实现了一个全局唯一 ID 生成器，适用于分布式系统中需要生成唯一标识符的场景。
 */

/**
 * 1. 类的定义与依赖注入：
 *
 * @Component：将该类声明为 Spring 的组件，使其能够被 Spring 容器管理和自动扫描。
 * @Autowired：自动注入 StringRedisTemplate，这是 Spring 提供的用于操作 Redis 的模板类。
 */
@Component
public class RedisIdWorker {
    /**
     * 2. 常量定义：
     * BEGIN_TIMESTAMP：定义了一个起始时间戳，表示从该时间点开始计算。
     * 这里的值 1640995200L 对应的是 2022 年 1 月 1 日 00:00:00 的 UNIX 时间戳（以秒为单位）。
     * COUNT_BIT：表示序列号占用的位数，这里设置为 32 位。
     */
    // 设置起始时间，我这里设定的是2022.01.01 00:00:00
    public static final Long BEGIN_TIMESTAMP = 1640995200L;
    // 序列号长度
    public static final Long COUNT_BIT = 32L;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * nextId 方法：
     *
     * @param keyPrefix
     * @return
     */
    public long nextId(String keyPrefix) {
        /* 1. 生成时间戳
        LocalDateTime.now()：获取当前的本地日期时间。
        now.toEpochSecond(ZoneOffset.UTC)：将当前时间转换为 UTC 时区的秒级时间戳。
        timeStamp = currentSecond - BEGIN_TIMESTAMP：计算当前时间与起始时间的差值，得到从起始时间到现在的秒数。
         */
        LocalDateTime now = LocalDateTime.now();
        long currentSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = currentSecond - BEGIN_TIMESTAMP;
        /* 2. 生成序列号
        now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"))：
        将当前日期格式化为字符串（格式为 "yyyy:MM:dd"），用于构造 Redis 的键。
        stringRedisTemplate.opsForValue().increment("inc:" + keyPrefix + ":" + date)：
        在 Redis 中以 "inc:" + keyPrefix + ":" + date 为键，
        对其值执行自增操作。该键的命名方式确保了不同业务（通过 keyPrefix 区分）在不同日期下的计数是独立的。
         */
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment("inc:" + keyPrefix + ":" + date);
        // 3. 拼接并返回，简单位运算
        return timeStamp << COUNT_BIT | count;
    }
}
