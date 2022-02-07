package com.matrix.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.matrix.gmall.payment.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * AliPayController
 *
 * @author yihaosun
 * @date 2022/2/7 20:17
 */
@Controller
@RestController("/api/payment/alipay")
public class AliPayController {
    @Autowired
    private AlipayService alipayService;

    /**
     * 注解:@ResponseBody作用
     * 第一个：返回数据是Json
     * 第二个：直接将数据输入到页面！
     *
     * @param orderId orderId
     * @return String
     */
    @RequestMapping("submit/{orderId}")
    @ResponseBody
    public String aliPay(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        return alipayService.createAliPay(orderId);
    }
}
