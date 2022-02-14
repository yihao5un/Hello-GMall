package com.matrix.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.model.enums.OrderStatus;
import com.matrix.gmall.model.enums.ProcessStatus;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.matrix.gmall.model.enums.OrderStatus.DELEVERED;

/**
 * @author yihaosun
 * @date 2022/1/29 11:23
 */
@Component
public class OrderReceiver {
    @Autowired
    private OrderService orderService;

    /**
     * 取消订单的业务逻辑
     * @param orderId orderId
     */
    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void orderCancel(Long orderId) {
        // 取消订单的业务逻辑
        // TODO 制作配置类 设置队列、交换机以及绑定关系 {@link com.matrix.gmall.order.config.OrderCancelMqConfig}
        if (Objects.nonNull(orderId)) {
            OrderInfo orderInfo = orderService.getById(orderId);
            // 订单状态和支付状态都为未付款的
            if (Objects.nonNull(orderInfo) && OrderStatus.CLOSED.name().equals(orderInfo.getOrderStatus()) && OrderStatus.CLOSED.name().equals(orderInfo.getProcessStatus())) {
                // 修改订单状态 -> 变成CLOSED
                orderService.execExpiredOrder(orderId);
                // TODO 还需要修改支付宝的状态
            }
        }
    }

    /**
     * 更新订单状态
     * @param orderId orderId
     * @param message message
     * @param channel channel
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_PAYMENT_PAY, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
            key = {MqConst.ROUTING_PAYMENT_PAY}
    ))
    public void paymentPay(Long orderId, Message message, Channel channel) {
        if (Objects.nonNull(orderId)) {
            // 更新订单状态
            OrderInfo orderInfo = orderService.getById(orderId);
            // 订单状态和支付状态都为未付款的
            if (Objects.nonNull(orderInfo) && OrderStatus.CLOSED.name().equals(orderInfo.getOrderStatus()) && OrderStatus.CLOSED.name().equals(orderInfo.getProcessStatus())) {
                // 修改订单状态 -> 变成PAID
                orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
                // 发送消息给库存 减少库存数量
                orderService.sendOrderStatus(orderId);
            }
        }
        // 手动确认ACK
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听库存系统发送的减库存结果
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_WARE_ORDER, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_WARE_ORDER),
            key = {MqConst.ROUTING_WARE_ORDER}
    ))
    public void updateOrderStatus(String wareJson, Message message, Channel channel) {
        if (StringUtils.isNotEmpty(wareJson)) {
            // 转换为JsonObject
            Map map = JSON.parseObject(wareJson, Map.class);
            String orderId = (String) map.get("orderId");
            String status = (String) map.get("status");
            if (OrderStatus.DELEVERED.name().equals(status)) {
                // 减库存 更新订单状态 待发货
                orderService.updateOrderStatus(Long.parseLong(orderId) , ProcessStatus.WAITING_DELEVER);
            } else {
                // 异常情况下 1.补货 2.通知客户
                orderService.updateOrderStatus(Long.parseLong(orderId) , ProcessStatus.STOCK_EXCEPTION);
                // 订单 --- 支付 --- 库存 可以看作是分布式事务(但是不是强一致性的) 用MQ解决 可以保证最终一致性

            }
        }
        // 手动确认ACK
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
