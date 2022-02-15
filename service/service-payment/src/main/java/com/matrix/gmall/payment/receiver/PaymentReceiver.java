package com.matrix.gmall.payment.receiver;

import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.payment.service.PaymentService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yihaosun
 * @date 2022/2/15 14:56
 */
@Component
public class PaymentReceiver {
    @Autowired
    private PaymentService paymentService;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_CLOSE, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE),
            key = {MqConst.ROUTING_PAYMENT_CLOSE}
    ))
    public void closePaymentInfo(Long orderId, Message message, Channel channel) {
        if (Objects.nonNull(orderId)) {
            // 关闭交易记录方法
            paymentService.closePaymentInfo(orderId);
        }
        // ACK
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false );
    }
}
