package com.matrix.gmall.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 基于插件实现延迟队列
 * 三个Bean 一个队列、一个交换机还有一个绑定关系
 *
 * 使用插件的时候 不需要对队列进行设置
 * Map<String, Object> map = new HashMap<>();
 * map.put("x-dead-letter-exchange", EXCHANGE_DEAD);
 * map.put("x-dead-letter-routing-key", ROUTING_DEAD_2);
 * //  设置消息的TTL
 * map.put("x-message-ttl", 10000);
 * // 队列名称，是否持久化，是否独享、排外的【true:只可以在本次连接中访问】，是否自动删除，队列的其他属性参数
 *
 *
 * @author yihaosun
 * @date 2022/1/28 16:23
 */
@Configuration
public class DelayedMqConfig {
    public static final String EXCHANGE_DELAY = "exchange.delay";
    public static final String ROUTING_DELAY = "routing.delay";
    public static final String QUEUE_DELAY_1 = "queue.delay.1";

    @Bean
    public Queue delayQueue() {
        // TODO 延迟时间在哪里设置? 只能使用rabbitTemplate去设置 {@link com.matrix.gmall.mq.controller#sendDelay()}
        return new Queue(QUEUE_DELAY_1, true,  false, false);
    }

    @Bean
    public CustomExchange delayExchange() {
        HashMap<String, Object> map = new HashMap<>(10);
        map.put("x-delayed-type", "direct");
        return new CustomExchange(EXCHANGE_DELAY,  "x-delayed-message", true,  false, map);
    }

    @Bean
    public Binding delayBinding() {
        // 注意返回的类型要加上 noargs() 方法
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(ROUTING_DELAY).noargs();
    }
}
