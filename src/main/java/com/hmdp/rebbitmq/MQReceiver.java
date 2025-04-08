package com.hmdp.rebbitmq;
/**
 * 定义了一个名为 MQReceiver 的服务类，主要用于处理秒杀（抢购）订单的消息接收和处理。
 * s它利用了 Spring Boot、RabbitMQ 和 MyBatis 等技术，监听 RabbitMQ 队列中的消息，处理订单，并更新库存。
 */

import com.alibaba.fastjson.JSON;
import com.hmdp.config.RabbitMQTopicConfig;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 消息消费者
 */

/**
 * 1. 类和注解说明
 *
 * @Slf4j：这是 Lombok 提供的注解，用于在类中自动生成一个日志记录器（log），方便进行日志记录。
 * @Service：将该类标识为 Spring 的服务组件，使其成为 Spring 容器管理的 Bean，可以被其他组件注入使用。
 */
@Slf4j
@Service
public class MQReceiver {

    /**
     * 2. 依赖注入
     *
     * @Resource：用于注入依赖的服务组件。这里注入了两个服务： IVoucherOrderService voucherOrderService：处理代金券订单相关操作的服务。
     * ISeckillVoucherService seckillVoucherService：处理秒杀代金券库存操作的服务。
     */
    @Resource
    IVoucherOrderService voucherOrderService;

    @Resource
    ISeckillVoucherService seckillVoucherService;

    /**
     * 接收秒杀信息并下单
     *
     * @param msg
     */

    /**
     * 5. 事务管理
     *
     * @Transactional： 该注解确保方法内的数据库操作在一个事务中执行。
     * 如果方法执行过程中发生异常，事务会回滚，确保数据的一致性和完整性。
     */
    @Transactional
    /**
     * 3. 消息监听与处理
     * @RabbitListener(queues = RabbitMQTopicConfig.QUEUE)：
     * 该注解使方法成为 RabbitMQ 消息队列的监听器，监听指定队列（RabbitMQTopicConfig.QUEUE）中的消息，
     * 并在有消息时触发 receiveSeckillMessage 方法。
     */
    @RabbitListener(queues = RabbitMQTopicConfig.QUEUE)
    /**
     * 4. 方法逻辑
     * 接收和解析消息：方法接收一个字符串类型的消息（msg），并将其解析为 VoucherOrder 对象。
     * 获取代金券 ID 和用户 ID：从 voucherOrder 对象中获取 voucherId（代金券 ID）和 userId（用户 ID）。
     * 检查用户是否已购买该代金券：
     *      通过调用 voucherOrderService，查询数据库中是否存在相同用户和代金券的订单记录。
     *      如果存在，表示用户已经购买过该代金券，记录错误日志并返回，避免重复购买。
     * 扣减库存：
     *      调用 seckillVoucherService，尝试更新代金券的库存数量。使用乐观锁（CAS）确保库存更新的原子性，
     *      即只有在库存大于 0 时，才会将库存减 1。
     *
     *      如果库存不足，记录错误日志并返回。
     *
     * 保存订单：如果库存更新成功，调用 voucherOrderService.save(voucherOrder) 将订单保存到数据库。
     */
    public void receiveSeckillMessage(String msg) {
        log.info("接收到消息: " + msg);
        VoucherOrder voucherOrder = JSON.parseObject(msg, VoucherOrder.class);

        Long voucherId = voucherOrder.getVoucherId();
        //5.一人一单
        Long userId = voucherOrder.getUserId();
        //5.1查询订单
        int count = voucherOrderService.query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        //5.2判断是否存在
        if (count > 0) {
            //用户已经购买过了
            log.error("该用户已购买过");
            return;
        }
        log.info("扣减库存");
        //6.扣减库存
        boolean success = seckillVoucherService
                .update()
                .setSql("stock = stock-1")
                .eq("voucher_id", voucherId)
                .gt("stock", 0)//cas乐观锁
                .update();
        if (!success) {
            log.error("库存不足");
            return;
        }
        //直接保存订单
        voucherOrderService.save(voucherOrder);
    }

}
/**
 * 该类主要负责处理秒杀订单的消息，包括验证用户购买资格、扣减库存和保存订单等操作。通过使用 RabbitMQ 监听消息、
 * MyBatis 进行数据库操作，以及 Spring 的事务管理，确保了秒杀过程的高效性和数据的一致性。
 */