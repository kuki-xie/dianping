package com.hmdp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 一个标准的 Spring Boot 启动类，是在写一个基于 Spring Boot + MyBatis 的项目
 */

@MapperScan("com.hmdp.mapper")
/**
 * 扫描 MyBatis 的 Mapper 接口
 * @MapperScan 是 MyBatis 提供的注解，用来自动扫描指定包下的接口（Mapper 接口），并将它们注册为 Spring Bean。
 *
 * 换句话说，这样你就不需要在每个 Mapper 接口上都写 @Mapper 注解了。
 *
 * "com.hmdp.mapper" 就是你项目中放置 Mapper 接口的包。
 */
@SpringBootApplication
/**
 * 标记这是一个 Spring Boot 应用
 * 这个注解其实是一个复合注解，等价于这三个：
 * @Configuration        // 标记为配置类
 * @EnableAutoConfiguration // 启动自动配置机制
 * @ComponentScan       // 自动扫描当前包及子包中的组件（@Component、@Service、@Repository、@Controller 等）
 * 这意味着：
 *
 * Spring 会自动根据你的依赖去配置好 Web、数据库连接池、MVC 等。
 *
 * 也会自动扫描并加载你的 Bean（比如 Service、Controller）。
 */
public class HmDianPingApplication { //
    /**
     * 这是你的应用主类（入口）
     * 类名一般是项目名 + Application 的形式，约定俗成。
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }
    /**
     * 启动方法
     * 这是 Java 应用的入口。Spring Boot 就是通过这行代码启动整个应用。
     *
     * SpringApplication.run(...) 会初始化 Spring 上下文、加载配置、启动 Web 服务器等。
     *
     * 启动成功后，就可以通过浏览器访问接口，或执行相关逻辑了。
     */

}
