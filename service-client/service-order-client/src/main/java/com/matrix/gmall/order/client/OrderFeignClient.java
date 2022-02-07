package com.matrix.gmall.order.client;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.client.imp.OrderDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 发布接口到Feign上供给Web-all去使用
 *
 * @Author: yihaosun
 * @Date: 2021/12/4 12:48
 */
@FeignClient(value = "service-order", fallback = OrderDegradeFeignClient.class)
public interface OrderFeignClient {
    /**
     * 确认订单
     *
     * @return Map<>
     */
    @GetMapping("/api/order/auth/trade")
    Result<Map<String, Object>> trade();

    /**
     * 获取订单信息接口
     *
     * @param orderId orderId
     * @return OrderInfo
     */
    @GetMapping("/api/order/inner/getOrderInfo/{orderId}")
    OrderInfo getOrderInfo(@PathVariable(value = "orderId") Long orderId);
}
