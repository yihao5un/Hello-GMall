package com.matrix.gmall.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列MQ配置
 *
 * 发生条件:
 *  （1） 一个消息被Consumer拒收了，并且reject方法的参数里requeue是false。也就是说不会被再次放在队列里，被其他消费者使用。
 *  （2）上面的消息的TTL到了，消息过期了。
 *  （3）队列的长度限制满了。排在前面的消息会被丢弃或者扔到死信路由上。
 *
 * @author yihaosun
 * @date 2022/1/27 16:45
 */
@Configuration
public class DeadLetterMqConfig {
    /**
     * 定义变量
     */
    public static final String EXCHANGE_DEAD = "exchange.dead";
    public static final String ROUTING_DEAD_1 = "routing.dead.1";
    public static final String ROUTING_DEAD_2 = "routing.dead.2";
    public static final String QUEUE_DEAD_1 = "queue.dead.1";
    public static final String QUEUE_DEAD_2 = "queue.dead.2";

    /**
     * 声明一个交换机
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_DEAD, true, false);
    }

    /**
     * 正常情况下声明一个队列
     */
    @Bean
    public Queue queue1() {
        Map<String, Object> map = new HashMap<>();
        map.put("x-dead-letter-exchange", EXCHANGE_DEAD);
        map.put("x-dead-letter-routing-key", ROUTING_DEAD_2);
        //  设置消息的TTL
        map.put("x-message-ttl", 10000);
        // 队列名称，是否持久化，是否独享、排外的【true:只可以在本次连接中访问】，是否自动删除，队列的其他属性参数
        return new Queue(QUEUE_DEAD_1, true, false, false, map);
    }

    /**
     * 正常情况下绑定队列和交换机
     */
    @Bean
    public Binding binding1() {
        // 正常情况下的绑定
        return BindingBuilder.bind(queue1()).to(exchange()).with(ROUTING_DEAD_1);
    }

    /**
     * 死信情况下声明一个队列
     */
    @Bean
    public Queue queue2() {
        return new Queue(QUEUE_DEAD_2, true, false, false, null);
    }

    /**
     * 死信情况下绑定队列和交换机
     */
    @Bean
    public Binding binding2() {
        // 正常情况下的绑定
        return BindingBuilder.bind(queue2()).to(exchange()).with(ROUTING_DEAD_2);
    }
}
