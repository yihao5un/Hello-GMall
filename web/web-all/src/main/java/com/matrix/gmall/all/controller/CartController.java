package com.matrix.gmall.all.controller;

import com.matrix.gmall.cart.client.CartFeignClient;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sun.jvm.hotspot.ui.tree.SimpleTreeGroupNode;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yihaosun
 * @Date: 2021/11/1 11:22
 */
@Controller
public class CartController {
    /**
     * 远程调用service-cart-client
     */
    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @GetMapping("addCart.html")
    public String addCart(HttpServletRequest request) {
        String skuId = request.getParameter("skuId");
        String skuNum = request.getParameter("skuNum");
        cartFeignClient.addToCart(Long.parseLong(skuId), Integer.parseInt(skuNum));
        SkuInfo skuInfo = productFeignClient.getSkuInfo(Long.parseLong(skuId));
        // 设置SkuInfo和SkuNum
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        return "cart/addCart";
    }

    @GetMapping("cart.html")
    public String cartPage() {
        // 返回购物车列表页面
        return "cart/index";
    }
}
