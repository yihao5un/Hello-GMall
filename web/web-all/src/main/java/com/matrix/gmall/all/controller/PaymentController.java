package com.matrix.gmall.all.controller;

import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yihaosun
 * @date 2022/2/7 15:16
 */
@Controller
public class PaymentController {
    @Qualifier("com.matrix.gmall.order.client.OrderFeignClient")
    @Autowired
    private OrderFeignClient orderFeignClient;

    @GetMapping("pay.html")
    public String pay(HttpServletRequest request) {
        // 获取orderId
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(Long.parseLong(orderId));
        // 保存对象
        request.setAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }
}
