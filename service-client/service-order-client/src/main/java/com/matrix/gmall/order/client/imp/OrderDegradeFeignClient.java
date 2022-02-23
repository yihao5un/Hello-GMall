package com.matrix.gmall.order.client.imp;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.client.OrderFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/12/4 12:49
 */
@Component
public class OrderDegradeFeignClient implements OrderFeignClient {
    @Override
    public Result<Map<String, Object>> trade() {
        return null;
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        return null;
    }

    @Override
    public Long submitOrder(OrderInfo orderInfo) {
        return null;
    }
}
