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

    /**
     * 退款
     *
     * @param orderId orderId
     * @return boolean
     */
    boolean refund(Long orderId);

    /**
     * 关闭交易
     *
     * @param orderId orderId
     * @return boolean
     */
    boolean closePay(Long orderId);

    /**
     * 查询支付宝是否有交易记录
     *
     * @param orderId orderId
     * @return Boolean
     */
    boolean checkPayment(Long orderId);
}
