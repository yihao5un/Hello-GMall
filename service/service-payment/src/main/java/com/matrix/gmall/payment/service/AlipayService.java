package com.matrix.gmall.payment.service;

import com.alipay.api.AlipayApiException;

/**
 * 支付宝支付
 *
 * @author yihaosun
 * @date 2022/2/7 19:33
 */
public interface AlipayService {
    /**
     * 支付宝支付
     *
     * @param orderId orderId
     * @throws AlipayApiException AlipayApiException
     * @return String
     */
    String createAliPay(Long orderId) throws AlipayApiException;
}
