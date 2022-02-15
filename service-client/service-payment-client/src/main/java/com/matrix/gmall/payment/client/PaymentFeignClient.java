package com.matrix.gmall.payment.client;

import com.matrix.gmall.model.payment.PaymentInfo;
import com.matrix.gmall.payment.client.impl.PaymentFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author yihaosun
 * @date 2022/2/15 16:06
 */
@FeignClient(value = "service-payment", fallback = PaymentFeignClientImpl.class)
public interface PaymentFeignClient {
    /**
     * 关闭支付宝交易记录
     *
     * @param orderId orderId
     * @return Boolean
     */
    @GetMapping("api/payment/alipay/closePay/{orderId}")
    Boolean closePay(@PathVariable Long orderId);

    /**
     * 查询支付宝是否有交易记录
     *
     * @param orderId orderId
     * @return Boolean
     */
    @GetMapping("api/payment/alipay/checkPayment/{orderId}")
    Boolean checkPayment(@PathVariable Long orderId);

    /**
     * 查询电商本地是否有交易记录
     * @param outTradeNo outTradeNo
     * @return PaymentInfo
     */
    @GetMapping("api/payment/alipay/getPaymentInfo/{outTradeNo}")
    PaymentInfo getPaymentInfo(@PathVariable String outTradeNo);
}
