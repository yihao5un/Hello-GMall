package com.matrix.gmall.mq.receiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 监听消息消费
 * @Author: yihaosun
 * @Date: 2022/1/25 17:00
 */
@Component
public class ConfirmReceiver {
    @RabbitListener(bindings = @QueueBinding(
            // 这个value才是队列的名 在消费端指定
            // 是否持久化durable 和 自动删除autoDelete
            value = @Queue(value = "queue.confirm", durable = "true", autoDelete = "false"),
            // exchange是发送的交换机
            exchange = @Exchange(value = "exchange.confirm"),
            // routingKey是发送的routingKey
            key = {"routing.confirm"}
    ))
    public void getMsg(String msg, Message message, Channel channel) throws IOException {
        System.out.println("接收到的消息: \t" + new String(message.getBody()));
        System.out.println("接收到的消息: \t" + msg);
        // 手动确认 ACK
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // false 表示一个一个确认 true 表示批量确认
        channel.basicAck(deliveryTag, true);
    }
}
