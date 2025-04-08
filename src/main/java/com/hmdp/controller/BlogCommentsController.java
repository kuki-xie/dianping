package com.hmdp.controller;
/**
 * 使用 Spring Boot 框架的控制器类，主要用于处理与博客评论相关的 HTTP 请求。
 */

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */

/**
 * 1. @RestController 注解
 * 作用：这是一个组合注解，结合了 @Controller 和 @ResponseBody 的功能。
 * 它表明该类是一个控制器，其中的每个方法的返回值都会直接作为 HTTP 响应的主体内容。
 * 这对于构建 RESTful API 非常有用，因为它简化了将 Java 对象转换为 HTTP 响应的过程。
 */
@RestController
@RequestMapping("/blog-comments")
/**
 * 2. @RequestMapping("/blog-comments") 注解
 * 作用：该注解用于映射特定的 URL 路径到控制器。
 * 在类级别上使用时，它定义了一个基础路径，应用于该控制器中的所有处理方法。
 * 在这个例子中，/blog-comments 是基础路径，因此该控制器中的所有方法都会处理以 /blog-comments 开头的请求。
 */

/**
 * 3. BlogCommentsController 类
 * 作用：这是一个标准的 Java 类，作为 Spring MVC 的控制器。
 * 它将包含处理博客评论相关请求的方法，例如添加评论、删除评论、获取特定博客的所有评论等
 */
public class BlogCommentsController {

}
/**
 * 通过使用 @RestController 和 @RequestMapping 注解，BlogCommentsController 类被配置为处理与博客评论相关的 RESTful API 请求。
 * 类级别的 @RequestMapping("/blog-comments") 定义了基础路径，使得该控制器中的所有方法都以 /blog-comments 为前缀。
 * 这种结构清晰地组织了与博客评论相关的端点，便于客户端与服务器之间的交互。
 */