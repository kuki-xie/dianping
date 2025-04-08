package com.hmdp.config;
/**
 * Spring Boot 配置类，旨在将 Redisson 客户端集成到应用中，以便与单节点的 Redis 服务器进行交互。
 */

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1. @Configuration 注解
 * 作用：标识该类为一个配置类，Spring 在启动时会自动加载并应用其中的配置。
 */
@Configuration
public class RedissonConfig {
    /**
     * 2. redissonClient 方法
     * 返回值：RedissonClient 类型的 Bean。
     * 作用：创建并配置 RedissonClient，用于与 Redis 服务器进行交互。
     *
     * @return
     */

    /**
     * 4. 将 RedissonClient 注册为 Bean
     *
     * @return
     * @Bean 注解：将 redissonClient 方法的返回值注册为 Spring 容器中的 Bean，
     * 以便在应用的其他部分可以通过依赖注入使用 RedissonClient。
     */
    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();// 创建一个新的 Config 对象，用于配置 Redisson。
//        根据配置创建并返回 RedissonClient 实例。
//        config.useSingleServer()：指定使用单节点模式连接 Redis。
//        Redisson 支持多种模式，包括单节点、集群、哨兵等模式。这里选择单节点模式。
        config.useSingleServer()// 说明当前用的是单节点的redis
                .setAddress("redis://localhost:6379").setPassword("123456");
        // 根据配置创建并返回 RedissonClient 实例。
        return Redisson.create(config);
    }
}
/**
 * 注意事项
 * Redis 服务器地址：在实际应用中，setAddress 中的地址应根据实际部署的 Redis 服务器地址进行修改。
 * 如果 Redis 服务器设置了密码，还需要调用 setPassword 方法进行配置。
 * Redisson 的模式选择：根据 Redis 部署模式的不同，Redisson 提供了多种配置方法，例如：
 * 集群模式：使用 config.useClusterServers() 配置。
 * 哨兵模式：使用 config.useSentinelServers() 配置。
 */

/**
 * 总结
 * 这段代码通过定义一个配置类 RedissonConfig，在 Spring Boot 应用中创建并配置了一个 RedissonClient，
 * 用于连接本地的单节点 Redis 服务器。
 * 通过将 RedissonClient 注册为 Spring 的 Bean，应用的其他组件可以方便地使用它来操作 Redis，实现分布式锁、缓存等功能。
 */