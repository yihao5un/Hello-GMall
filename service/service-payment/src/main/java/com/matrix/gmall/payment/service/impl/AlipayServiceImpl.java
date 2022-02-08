package com.matrix.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.matrix.gmall.model.enums.PaymentStatus;
import com.matrix.gmall.model.enums.PaymentType;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.model.payment.PaymentInfo;
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

    @Override
    public boolean refund(Long orderId) {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        // 声明退款请求对象
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        // 构建Json 字符串
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        // 退款金额 < 支付金额
        map.put("refund_amount",orderInfo.getTotalAmount());
        map.put("refund_reason","退款");
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        assert response != null;
        if(response.isSuccess()){
            System.out.println("调用成功");
            // 电商平台订单关闭！
            PaymentInfo updPaymentInfo = new PaymentInfo();
            updPaymentInfo.setPaymentStatus(PaymentStatus.CLOSED.name());
            this.paymentService.updatePaymentInfo(orderInfo.getOutTradeNo(),PaymentType.ALIPAY.name(),updPaymentInfo);
            // 订单状态关闭！
            return true;
        } else {
            System.out.println("调用失败");
            return false;
        }
    }
}
