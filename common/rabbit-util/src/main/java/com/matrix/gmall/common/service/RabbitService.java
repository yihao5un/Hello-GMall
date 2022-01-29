package com.matrix.gmall.common.service;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 封装发送消息的方法
 * 
 * @Author: yihaosun
 * @Date: 2022/1/25 16:51
 */
@Service
public class RabbitService {
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

    /**
     * 发送延迟消息(基于插件)的方法
     *
     * @param exchange exchange
     * @param routingKey routingKey
     * @param message message
     * @param delayTime delayTime
     * @return true
     */
    public boolean sendDelayMessage(String exchange, String routingKey, Object message, Integer delayTime) {
        // 发送消息
        rabbitTemplate.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置延迟时间(变成秒级别)
                message.getMessageProperties().setDelay(delayTime * 1000);
                return message;
            }
        });
        return true;
    }
}
