package com.hmdp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 配置类，旨在配置 RabbitMQ 的主题交换机（Topic Exchange）、队列（Queue）以及它们之间的绑定关系。
 * SpringBoot动态创建绑定rabbitMq队列
 * 只需要在配置文件中配置队列、交换机等信息，就可以在服务启动的时候自动创建并绑定。
 */
@Configuration
public class RabbitMQTopicConfig {
    /**
     * 1. 定义常量
     * QUEUE：定义队列的名称为 seckillQueue。
     * EXCHANGE：定义交换机的名称为 seckillExchange。
     * ROUTINGKEY：定义路由键模式为 seckill.#，其中 # 是 RabbitMQ 中的通配符，表示匹配零个或多个单词。
     */
    public static final String QUEUE = "seckillQueue";
    public static final String EXCHANGE = "seckillExchange";
    public static final String ROUTINGKEY = "seckill.#";

    /**
     * 2. 声明队列
     * 通过 @Bean 注解，将方法返回的 Queue 对象注册为 Spring 容器中的 Bean。
     * 创建一个名为 seckillQueue 的队列。
     *
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }

    /**
     * 3. 声明主题交换机
     * 同样通过 @Bean 注解，将方法返回的 TopicExchange 对象注册为 Bean。
     * 创建一个名为 seckillExchange 的主题交换机。
     *
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    /**
     * 4. 绑定队列到交换机
     * 使用 BindingBuilder 将队列绑定到交换机，并指定路由键模式。
     * 这里的路由键模式 seckill.# 表示凡是以 seckill. 开头的路由键，都会匹配到该队列。
     * 例如，seckill.order、seckill.user.info 等都会被路由到 seckillQueue 队列。
     *
     * @return
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with(ROUTINGKEY);
    }
//    private static final String QUEUE01="queue_topic01";
//    private static final String QUEUE02="queue_topic02";
//    private static final String EXCHANGE = "topicExchange";
//    private static final String ROUTINGKEY01 = "#.queue.#";
//    private static final String ROUTINGKEY02 = "*.queue.#";
//    @Bean
//    public Queue topicqueue01(){
//        return new Queue(QUEUE01);
//    }
//    @Bean
//    public Queue topicqueue02(){
//        return new Queue(QUEUE02);
//    }
//    @Bean
//    public TopicExchange topicExchange(){
//        return new TopicExchange(EXCHANGE);
//    }
//    @Bean
//    public Binding topicbinding01(){
//        return BindingBuilder.bind(topicqueue01()).to(topicExchange()).with(ROUTINGKEY01);
//    }
//    @Bean
//    public Binding topicbinding02(){
//        return BindingBuilder.bind(topicqueue02()).to(topicExchange()).with(ROUTINGKEY02);
//    }
}
