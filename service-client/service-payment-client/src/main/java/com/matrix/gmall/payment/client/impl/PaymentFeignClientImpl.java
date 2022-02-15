package com.matrix.gmall.payment.client.impl;

import com.matrix.gmall.model.payment.PaymentInfo;
import com.matrix.gmall.payment.client.PaymentFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author yihaosun
 * @date 2022/2/15 16:08
 */
@Component
public class PaymentFeignClientImpl implements PaymentFeignClient {
    @Override
    public Boolean closePay(Long orderId) {
        return null;
    }

    @Override
    public Boolean checkPayment(Long orderId) {
        return null;
    }

    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo) {
        return null;
    }
}
