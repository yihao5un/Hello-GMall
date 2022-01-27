package com.matrix.gmall.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列MQ配置
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

//    @Bean
//    public Queue queue1() {
//        return new Queue();
//    }
}
