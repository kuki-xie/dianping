package com.hmdp.controller;
/**
 * 名为 ShopTypeController 的 Spring Boot 控制器类，主要用于处理与商铺类型相关的 HTTP 请求。
 */

import com.hmdp.dto.Result;
import com.hmdp.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 类级别注解
 *
 * @RestController：这是一个组合注解，等同于同时使用 @Controller 和 @ResponseBody。
 * 它的作用是将该类标识为一个控制器，并且其方法的返回值会直接作为 HTTP 响应体返回，通常用于构建 RESTful API。
 * @RequestMapping("/shop-type")：为该控制器指定基本的请求路径，即所有以 /shop-type 开头的请求都会由这个控制器处理。
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    /**
     * 依赖注入
     *
     * @Resource：这是 Java 提供的注解，用于按照名称或类型自动注入依赖对象。
     * 这里用于将 IShopTypeService 接口的实现注入到 typeService 字段中，以处理商铺类型相关的业务逻辑。
     */
    @Resource
    private IShopTypeService typeService;

    /**
     * 控制器方法
     * 查询商铺类型列表
     *
     * @return
     * @GetMapping("list")：处理对路径 /shop-type/list 的 HTTP GET 请求。
     * queryTypeList() 方法：调用 typeService 的 queryList() 方法，获取商铺类型的列表，
     * 并将结果封装在 Result 对象中返回。
     */
    @GetMapping("list")
    public Result queryTypeList() {
        return typeService.queryList();
    }
}
/**
 * 通过上述配置，ShopTypeController 提供了一个接口，允许客户端通过访问 /shop-type/list 获取商铺类型的列表信息。
 */
