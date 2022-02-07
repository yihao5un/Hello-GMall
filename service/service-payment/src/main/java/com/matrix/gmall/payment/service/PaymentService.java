package com.matrix.gmall.payment.service;

import com.matrix.gmall.model.order.OrderInfo;

/**
 * @author yihaosun
 * @date 2022/2/7 17:03
 */
public interface PaymentService {
    /**
     * 保存交易记录的接口
     *
     * @param orderInfo orderInfo
     * @param paymentType paymentType
     */
    void savePaymentInfo(OrderInfo orderInfo, String paymentType);
}
