package com.hmdp.controller;
/**
 * 名为 VoucherController 的 Spring Boot 控制器，主要负责处理与优惠券相关的操作，
 * 包括新增普通优惠券、新增秒杀优惠券，以及查询店铺的优惠券列表。
 */

import com.hmdp.dto.Result;
import com.hmdp.entity.Voucher;
import com.hmdp.service.IVoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 类级别注解
 *
 * @RestController：这是一个复合注解，等同于同时使用了 @Controller 和 @ResponseBody。它将类标识为一个控制器，
 * 并且所有方法的返回值都会直接作为 HTTP 响应体返回，通常用于构建 RESTful API。
 * @RequestMapping("/voucher")：为该控制器指定基础请求路径，即所有以 /voucher 开头的请求都会由此控制器处理。
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    /**
     * 依赖注入
     *
     * @Resource：用于自动注入依赖对象。 这里将 IVoucherService 的实现注入到 voucherService 字段中，
     * 负责处理优惠券相关的业务逻辑。
     */
    @Resource
    private IVoucherService voucherService;

    /**
     * 控制器方法解析
     * 1. 新增普通券
     *
     * @param voucher 优惠券信息
     * @return 优惠券id
     * @PostMapping：处理 /voucher 的 POST 请求，用于新增普通优惠券。
     * addVoucher 方法：
     * 参数：voucher，通过 @RequestBody 注解，从请求体中获取优惠券信息。
     * 调用 voucherService 的 save 方法，将优惠券信息保存到数据库。
     * 返回保存后的优惠券 ID。
     */
    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher) {
        voucherService.save(voucher);
        return Result.ok(voucher.getId());
    }

    /**
     * 2. 新增秒杀券
     *
     * @param voucher 优惠券信息，包含秒杀信息
     * @return 优惠券id
     * @PostMapping("seckill")：处理 /voucher/seckill 的 POST 请求，用于新增秒杀优惠券。
     * addSeckillVoucher 方法：
     * 参数：voucher，通过 @RequestBody 注解，从请求体中获取包含秒杀信息的优惠券。
     * 调用 voucherService 的 addSeckillVoucher 方法，处理秒杀优惠券的特定逻辑并保存到数据库。
     * 返回保存后的优惠券 ID。
     */
    @PostMapping("seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.ok(voucher.getId());
    }

    /**
     * 3. 查询店铺的优惠券列表
     *
     * @param shopId 店铺id
     * @return 优惠券列表
     * @GetMapping("/list/{shopId}")：处理 /voucher/list/{shopId} 的 GET 请求，用于查询指定店铺的优惠券列表。
     * queryVoucherOfShop 方法：
     * 参数：shopId，通过 @PathVariable 注解，从 URL 路径中获取店铺 ID。
     * 调用 voucherService 的 queryVoucherOfShop 方法，获取该店铺的优惠券列表。
     * 返回查询到的优惠券列表。
     */
    @GetMapping("/list/{shopId}")
    public Result queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
        return voucherService.queryVoucherOfShop(shopId);
    }
}
/**
 * VoucherController 提供了与优惠券相关的 RESTful API 接口，
 * 包括新增普通优惠券、新增秒杀优惠券，以及查询店铺的优惠券列表。
 * 通过使用 @RestController 和 @RequestMapping 注解，定义了统一的请求路径前缀和响应方式。
 * 具体的业务逻辑由注入的 voucherService 实现，控制器主要负责处理 HTTP 请求和响应。
 */
