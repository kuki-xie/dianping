package com.hmdp.controller;
/**
 * 名为 VoucherOrderController 的 Spring Boot 控制器，主要用于处理与优惠券订单相关的操作，
 * 特别是秒杀优惠券的下单功能。
 */

import com.hmdp.dto.Result;
import com.hmdp.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类级别注解
 *
 * @RestController：这是一个复合注解，结合了 @Controller 和 @ResponseBody 的功能，
 * 表示该类是一个控制器，并且其方法的返回值会直接作为 HTTP 响应体返回，通常用于构建 RESTful API。
 * @RequestMapping("/voucher-order")： 为该控制器指定了基础请求路径， 即所有以 /voucher-order 开头的请求都会由此控制器处理。
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {
    /**
     * 依赖注入
     *
     * @Autowired：用于自动注入依赖对象。这里将 IVoucherOrderService 的实现注入到 voucherOrderService 字段中，
     * 以处理优惠券订单相关的业务逻辑。
     */
    @Autowired
    private IVoucherOrderService voucherOrderService;

    /**
     * 控制器方法解析
     * 1. 秒杀优惠券下单
     *
     * @param voucherId
     * @return
     * @PostMapping("seckill/{id}")：处理 /voucher-order/seckill/{id} 的 POST 请求，用于秒杀特定的优惠券。
     * seckillVoucher 方法：
     * 参数：voucherId，通过 @PathVariable("id") 注解，从 URL 路径中获取优惠券的 ID。
     * 调用 voucherOrderService 的 seckillVoucher 方法，执行秒杀下单的业务逻辑。
     * 返回秒杀操作的结果，通常是一个包含操作成功或失败信息的 Result 对象。
     */
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
/**
 * VoucherOrderController 提供了与优惠券订单相关的 RESTful API 接口，主要功能是处理秒杀优惠券的下单操作。
 * 通过使用 @RestController 和 @RequestMapping 注解，定义了统一的请求路径前缀和响应方式。
 * 具体的业务逻辑由注入的 voucherOrderService 实现，控制器主要负责处理 HTTP 请求和响应。
 */
