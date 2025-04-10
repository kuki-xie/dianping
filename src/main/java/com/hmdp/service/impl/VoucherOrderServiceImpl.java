package com.hmdp.service.impl;
/**
 * 实现了秒杀代金券的功能，涉及限流、Lua 脚本执行、订单创建和消息队列等多个环节。
 */

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.RateLimiter;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.rebbitmq.MQSender;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    /*
    1. 定义一个静态常量 SECKILL_SCRIPT，用于存储 Redis 脚本
    使用 DefaultRedisScript<Long> 类型，表示该脚本执行后返回值的类型为 Long。
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    /*
    2. 静态初始化块：
    在静态初始化块中，对 SECKILL_SCRIPT 进行实例化和配置。
     */
    static {
        /*
        实例化 DefaultRedisScript：
        创建 DefaultRedisScript 的实例，用于封装将要执行的 Lua 脚本。
        DefaultRedisScript 是 Spring Data Redis 提供的一个类，用于在 Redis 中执行脚本。
         */
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        /*
        设置脚本位置：
        setLocation 方法用于指定脚本的位置。这里使用了 ClassPathResource，表示脚本位于类路径下的 seckill.lua 文件。
        ClassPathResource 是 Spring 提供的一个类，用于访问类路径下的资源文件。
         */
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        /*
        setResultType 方法用于指定脚本执行后的返回值类型，这里设置为 Long.class，表示脚本返回的是一个长整型值。
         */
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * RateLimiter 的使用： rateLimiter 是基于 Google Guava 库中的 RateLimiter 类实例，
     * 采用令牌桶算法进行限流。
     * RateLimiter.create(10) 表示每秒生成 10 个令牌，即每秒允许最多 10 个请求通过。
     */
    private final RateLimiter rateLimiter = RateLimiter.create(10);
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private MQSender mqSender;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result seckillVoucher(Long voucherId) {
        /*
        tryAcquire 方法： 尝试在指定时间内获取一个令牌。这里设置了超时时间为 1000 毫秒（1 秒）。
        如果在超时时间内未能获取到令牌，说明当前请求过多，返回错误信息 "目前网络正忙，请重试"。
         */
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return Result.fail("目前网络正忙，请重试");
        }
        /*
         1.执行lua脚本
         获取用户 ID： 通过 UserHolder.getUser().getId() 获取当前登录用户的 ID。
         */
        Long userId = UserHolder.getUser().getId();
        /*
        执行 Lua 脚本： 使用 stringRedisTemplate 的 execute 方法执行预先定义的 Lua 脚本 SECKILL_SCRIPT。
        该脚本的主要功能包括：
        检查代金券库存是否充足。
        验证用户是否已经参与过此次秒杀（防止重复下单）。
        如果上述条件满足，扣减库存并记录用户的秒杀资格。
        Lua 脚本的执行保证了上述操作的原子性，避免了并发问题。
         */
        Long r = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString()
        );
        /* 2.判断结果
        结果判断： 将 Lua 脚本的返回值转换为整数 result。根据约定：
        返回 0 表示秒杀成功。
        返回 1 表示库存不足。
        返回 2 表示用户重复下单。
        处理失败情况： 如果 result 不为 0，根据返回值提供相应的错误提示信息。
         */
        int result = r.intValue();
        if (result != 0) {
            // 2.1不为0代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "该用户重复下单");
        }
        // 2.2为0代表有购买资格,将下单信息保存到阻塞队列

        // 3. 创建订单并发送消息
        // 订单对象创建： 如果秒杀资格验证通过，创建一个新的 VoucherOrder 对象。
        VoucherOrder voucherOrder = new VoucherOrder();
        /*
         生成订单 ID： 使用 redisIdWorker.nextId("order") 生成全局唯一的订单 ID。
         redisIdWorker 是一个基于 Redis 实现的分布式 ID 生成器，确保在分布式环境下生成唯一的 ID。
         */

        long orderId = redisIdWorker.nextId("order");
        // 设置订单属性： 将生成的订单 ID、用户 ID 和代金券 ID 设置到 voucherOrder 对象中。
        voucherOrder.setId(orderId);
        // 用户id
        voucherOrder.setUserId(userId);
        // 代金卷id
        voucherOrder.setVoucherId(voucherId);
        /*
        发送消息： 将 voucherOrder 对象转换为 JSON 字符串，
        将信息放入MQ中
         */
        mqSender.sendSeckillMessage(JSON.toJSONString(voucherOrder));


        // 返回订单id
        return Result.ok(orderId);

//        单机模式下，使用synchronized实现锁
//        synchronized (userId.toString().intern())
//        {
//            //    createVoucherOrder的事物不会生效,因为你调用的方法，其实是this.的方式调用的，事务想要生效，
//            //    还得利用代理来生效，所以这个地方，我们需要获得原始的事务对象， 来操作事务
//            return voucherOrderService.createVoucherOrder(voucherId);
//        }
    }


//    @Transactional
//    public Result createVoucherOrder(Long voucherId) {
//        // 一人一单逻辑
//        Long userId = UserHolder.getUser().getId();
//
//
//        int count = query().eq("voucher_id", voucherId).eq("user_id", userId).count();
//        if (count > 0){
//            return Result.fail("你已经抢过优惠券了哦");
//        }
//
//        //5. 扣减库存
//        boolean success = seckillVoucherService.update()
//                .setSql("stock = stock - 1")
//                .eq("voucher_id", voucherId)
//                .gt("stock",0)   //加了CAS 乐观锁，Compare and swap
//                .update();
//
//        if (!success) {
//            return Result.fail("库存不足");
//        }
//
////        库存足且在时间范围内的，则创建新的订单
//        //6. 创建订单
//        VoucherOrder voucherOrder = new VoucherOrder();
//        //6.1 设置订单id，生成订单的全局id
//        long orderId = redisIdWorker.nextId("order");
//        //6.2 设置用户id
//        Long id = UserHolder.getUser().getId();
//        //6.3 设置代金券id
//        voucherOrder.setVoucherId(voucherId);
//        voucherOrder.setId(orderId);
//        voucherOrder.setUserId(id);
//        //7. 将订单数据保存到表中
//        save(voucherOrder);
//        //8. 返回订单id
//        return Result.ok(orderId);
//    }
}
