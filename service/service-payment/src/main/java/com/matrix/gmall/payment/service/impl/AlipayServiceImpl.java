package com.matrix.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.matrix.gmall.model.enums.PaymentType;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.client.OrderFeignClient;
import com.matrix.gmall.payment.config.AlipayConfig;
import com.matrix.gmall.payment.service.AlipayService;
import com.matrix.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author yihaosun
 * @date 2022/2/7 19:35
 */
@Service
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    private AlipayClient alipayClient;

    @Qualifier("com.matrix.gmall.order.client.OrderFeignClient")
    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private PaymentService paymentService;


    @Override
    public String createAliPay(Long orderId) throws AlipayApiException {
        // 获取到orderInfo或者是paymentInfo
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        // 生产二维码的同时 保存交易记录 默认支付宝支付
        paymentService.savePaymentInfo(orderInfo, PaymentType.ALIPAY.name());
        // 1.  生产二维码！  取消订单： 5秒钟;
        // 2.  取消订单了，则不能生产二维码！
        if ("CLOSED".equals(orderInfo.getOrderStatus())) {
            return "该订单已经取消！";
        }
        // 获得初始化的AlipayClient 这个已经写成一个配置文件了 所以已经不需要创建这个类了 AlipayClient alipayClient =  new DefaultAlipayClient( "https://openapi.alipay.com/gateway.do" , APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 同步回调
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        // 异步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        // 需要json 字符串
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("out_trade_no", orderInfo.getOutTradeNo());
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", orderInfo.getTotalAmount());
        map.put("subject", orderInfo.getTradeBody());
        map.put("timeout_express", "5m");
        // 将map转换为JsonString
        String jsonStr = JSON.toJSONString(map);
        alipayRequest.setBizContent(jsonStr);
        return alipayClient.pageExecute(alipayRequest).getBody();
    }
}
