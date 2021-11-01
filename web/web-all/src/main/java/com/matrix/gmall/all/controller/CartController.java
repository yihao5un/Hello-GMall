package com.matrix.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: yihaosun
 * @Date: 2021/11/1 11:22
 */
@Controller
public class CartController {
    /**
     * 远程调用service-cart-client
     */

    @GetMapping("addCart.html")
    public String addCart() {
        // 返回视图
        return "cart/addCart";
    }
}
