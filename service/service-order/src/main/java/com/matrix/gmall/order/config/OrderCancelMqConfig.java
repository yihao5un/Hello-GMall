package com.matrix.gmall.order.config;

import com.matrix.gmall.common.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author yihaosun
 * @date 2022/1/29 15:03
 */
@Configuration
public class OrderCancelMqConfig {
    @Bean
    public Queue delayQueue() {
        return new Queue(MqConst.QUEUE_ORDER_CANCEL, true,  false, false);
    }

    @Bean
    public CustomExchange delayExchange() {
        HashMap<String, Object> map = new HashMap<>(10);
        map.put("x-delayed-type", "direct");
        return new CustomExchange(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL,  "x-delayed-message", true,  false, map);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(MqConst.ROUTING_ORDER_CANCEL).noargs();
    }
}
