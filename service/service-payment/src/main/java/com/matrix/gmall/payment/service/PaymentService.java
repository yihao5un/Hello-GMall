package com.matrix.gmall.payment.service;

import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.model.payment.PaymentInfo;

import java.util.Map;

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

    /**
     * 根据商户订单号查询交易记录
     *
     * @param outTradeNo outTradeNo
     * @param name name
     * @return PaymentInfo
     */
    PaymentInfo getPaymentInfo(String outTradeNo, String name);

    /**
     * 更新交易记录
     *
     * @param outTradeNo outTradeNo
     * @param name name
     * @param paramMap paramMap
     */
    void paySuccess(String outTradeNo, String name, Map<String, String> paramMap);

    /**
     * 内部封装更新交易记录
     *
     * @param outTradeNo outTradeNo
     * @param name name
     * @param paymentInfo paymentInfo
     */
    void updatePaymentInfo(String outTradeNo, String name, PaymentInfo paymentInfo);
}
