package com.matrix.gmall.cart.client.impl;

import com.matrix.gmall.cart.client.CartFeignClient;
import com.matrix.gmall.common.result.Result;
import org.springframework.stereotype.Component;

/**
 * @Author: yihaosun
 * @Date: 2021/11/13 19:50
 */
@Component
public class CartDegradeFeignClient implements CartFeignClient {
    @Override
    public Result addToCart(Long skuId, Integer skuNum) {
        return null;
    }
}
