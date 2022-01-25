package com.matrix.gmall.common.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: yihaosun
 * @Date: 2022/1/25 16:51
 */
@Service
public class RabbitService {
    /**
     * 编写发送消息的方法
     */

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 成功发送消息
     * @param exchange exchange
     * @param routingKey routingKey
     * @param message message
     * @return true
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        // 发送消息
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
