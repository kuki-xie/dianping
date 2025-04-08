package com.hmdp.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@Configuration 注解
//作用：标识该类是一个配置类，Spring 在启动时会自动加载并应用其中的配置。
public class MybatisConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //2. mybatisPlusInterceptor 方法
        //返回值：MybatisPlusInterceptor 类型的 Bean。
        //作用：创建并配置 MyBatis-Plus 的拦截器，用于添加插件功能。
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        3. MybatisPlusInterceptor 类
//作用：这是 MyBatis-Plus 提供的拦截器，用于扩展 MyBatis 的功能，可以添加多个内部拦截器（InnerInterceptor）
// 来实现不同的功能，例如分页、多租户、乐观锁等。
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        4. PaginationInnerInterceptor 类
//作用：这是一个内部拦截器，专门用于处理分页逻辑。它会拦截查询操作，自动拼接分页相关的 SQL，从而实现物理分页。
//
//参数：DbType.MYSQL 指定了数据库类型为 MySQL，这样插件就能生成适用于 MySQL 的分页 SQL。
        /**
         * 5. 配置流程
         * 创建 MybatisPlusInterceptor 实例。
         *
         * 添加 PaginationInnerInterceptor 到拦截器中，并指定数据库类型。
         *
         * 将配置好的拦截器作为一个 Bean 返回，交由 Spring 管理。
         */
        return interceptor;
        /**
         * 这段代码的主要目的是在 MyBatis-Plus 中配置分页插件，使得在使用 MyBatis 查询数据时，可以方便地进行分页操作，
         * 而无需手动编写分页 SQL。通过指定数据库类型为 MySQL，确保了分页插件生成的 SQL 与数据库兼容。
         */
    }
}
