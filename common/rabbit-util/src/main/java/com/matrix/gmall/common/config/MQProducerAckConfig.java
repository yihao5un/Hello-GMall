package com.matrix.gmall.common.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: yihaosun
 * @Date: 2021/12/21 20:56
 */
@Component
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    /**
     * 消息成功发送到交换机上
     *
     * @param correlationData 消息的载体 带有Id标示的
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            // 消息发送到了交换机
            System.out.println("消息发送成功!");
        } else {
            System.out.println("消息发送异常!");
        }
    }

    /**
     * @param message
     * @param i
     * @param s
     * @param s1
     * @param s2
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {

    }
}
