package com.matrix.gmall.order.receiver;

import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.model.enums.OrderStatus;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yihaosun
 * @date 2022/1/29 11:23
 */
@Component
public class OrderReceiver {
    @Autowired
    private OrderService orderService;

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
}
