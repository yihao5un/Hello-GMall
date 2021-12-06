package com.matrix.gmall.order.service;

import com.matrix.gmall.model.order.OrderInfo;

/**
 * @Author: yihaosun
 * @Date: 2021/12/6 22:28
 */
public interface OrderService {
    /**
     * 保存订单
     *
     * @param orderInfo  orderInfo
     * @return 返回的订单号
     */
    Long saveOrderInfo(OrderInfo orderInfo);
}
