package com.matrix.gmall.mq.receiver;

import com.matrix.gmall.mq.config.DeadLetterMqConfig;
import com.matrix.gmall.mq.config.DelayedMqConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 监听消息消费
 * @Author: yihaosun
 * @Date: 2022/1/25 17:00
 */
@Component
public class ConfirmReceiver {
    /**
     * 监听消息
     *
     * @param msg msg
     * @param message message
     * @param channel channel
     */
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
        try {
            System.out.println("接收到的消息: \t" + new String(message.getBody()));
            System.out.println("接收到的消息: \t" + msg);
            // 手动确认 ACK
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            // false 表示一个一个确认 true 表示批量确认
            channel.basicAck(deliveryTag, true);
        } catch (IOException e) {
            e.printStackTrace();
            // 消息消费异常 注意是nack
            // 第三个参数 是否重回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }
    }

    /**
     * 监听延迟队列消息
     *
     * @param msg msg
     * @param message message
     * @param channel channel
     */
    @RabbitListener(
            // 已经设置好了绑定关系 直接消费即可
            queues = DeadLetterMqConfig.QUEUE_DEAD_2)
    public void getDeadMsg(String msg, Message message, Channel channel) throws IOException {
        // 当前的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("接收到的消息: " + msg + "时间: \t" + simpleDateFormat.format(new Date()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听延迟队列消息 - 基于插件的方式
     * NOTE: 基于插件的会有一个小bug 会走returnedMessage()这个方法(消息如果没有成功发送到队列方法) 但是不影响业务
     *
     * @param msg msg
     * @param message message
     * @param channel channel
     */
    @RabbitListener(
            // 已经设置好了绑定关系 直接消费即可
            queues = DelayedMqConfig.QUEUE_DELAY_1)
    public void getDelayMsg(String msg, Message message, Channel channel) throws IOException {
        // 当前的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("接收到的消息: " + msg + "时间: \t" + simpleDateFormat.format(new Date()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
