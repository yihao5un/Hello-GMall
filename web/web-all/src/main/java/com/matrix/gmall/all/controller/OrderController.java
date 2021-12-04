package com.matrix.gmall.all.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/12/4 12:55
 */
@Controller
public class OrderController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * http://order.gmall.com/trade.html
     * @return 返回的是一个页面
     */
    @GetMapping("trade.html")
    public String trade(Model model) {
        Result<Map<String, Object>> result = orderFeignClient.trade();
        model.addAllAttributes(result.getData());
        // 订单页面
        return "order/trade";
    }
}
