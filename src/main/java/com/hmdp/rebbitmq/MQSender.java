package com.hmdp.rebbitmq;
/**
 * 定义了一个名为 MQSender 的服务类，主要用于向 RabbitMQ 消息队列发送秒杀（抢购）消息。
 * 它利用了 Spring AMQP 提供的 RabbitTemplate 来简化消息的发送过程。
 */

import com.hmdp.config.RabbitMQTopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 */

/**
 * 1. 类和注解说明
 *
 * @Slf4j：这是 Lombok 提供的注解，用于在类中自动生成一个日志记录器（log），方便进行日志记录。
 * @Service：将该类标识为 Spring 的服务组件，使其成为 Spring 容器管理的 Bean，可以被其他组件注入使用。
 */
@Slf4j
@Service
public class MQSender {

    /**
     * 3. 常量定义
     * ROUTINGKEY：定义了消息的路由键，用于确定消息发送到 RabbitMQ 中的哪个队列。
     * 这里设置为 "seckill.message"，表示秒杀消息。
     */
    private static final String ROUTINGKEY = "seckill.message";
    /**
     * 2. 依赖注入
     *
     * @Autowired：用于自动注入依赖的组件。这里注入了 RabbitTemplate，
     * 它是 Spring AMQP 提供的用于发送和接收消息的核心模板类。
     * RabbitTemplate：Spring AMQP 提供的核心组件，用于简化与 RabbitMQ 的交互。
     * 它封装了消息的发送、接收和转换等操作，使开发者可以像操作 JDBC 的 JdbcTemplate 一样方便地操作消息队列。
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     *
     * @param msg
     */

    /**
     * 4. 发送消息的方法
     * sendSeckillMessage(String msg)：该方法用于发送秒杀消息。
     * log.info("发送消息" + msg)：记录发送的消息内容。
     * rabbitTemplate.convertAndSend(RabbitMQTopicConfig.EXCHANGE, ROUTINGKEY, msg)：
     * 使用 RabbitTemplate 的 convertAndSend 方法发送消息。
     * convertAndSend：该方法将消息对象转换为适合传输的格式，并发送到指定的交换机和路由键对应的队列。
     * RabbitMQTopicConfig.EXCHANGE：消息发送到的交换机名称。
     *
     * @param msg
     */
    public void sendSeckillMessage(String msg) {
        log.info("发送消息" + msg);
        rabbitTemplate.convertAndSend(RabbitMQTopicConfig.EXCHANGE, ROUTINGKEY, msg);
    }
}
/**
 * 该类利用 Spring AMQP 提供的 RabbitTemplate，简化了向 RabbitMQ 发送秒杀消息的过程。通过定义交换机和路由键，
 * 可以将消息发送到指定的队列，方便消费者进行处理。这种方式提高了消息发送的效率和可靠性
 */