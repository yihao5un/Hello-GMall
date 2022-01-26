package com.matrix.gmall.common.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * RabbitMq 消息配置类
 *
 * @Author: yihaosun
 * @Date: 2021/12/21 20:56
 */
@Component
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 修饰一个非静态的void()方法,在服务器加载Servlet的时候运行并且只会被服务器执行一次
     * 在构造函数之后，init()方法之前执行。
     * 为了让当前这个类和rabbitTemplate关联上 注意这是一个坑！！！
     */
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 消息成功发送到交换机上
     *
     * @param correlationData 消息的载体 带有Id标示的
     * @param ack ack
     * @param cause cause
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
     * 消息如果没有成功发送到队列则执行当前这个方法
     * @param message message
     * @param replyCode replyCode
     * @param replyText replyText
     * @param exchange exchange
     * @param routingKey routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 反序列化对象输出
        System.out.println("消息主体: " + new String(message.getBody()));
        System.out.println("应答码: " + replyCode);
        System.out.println("描述：" + replyText);
        System.out.println("消息使用的交换器 exchange : " + exchange);
        System.out.println("消息使用的路由键 routing : " + routingKey);
    }
}
