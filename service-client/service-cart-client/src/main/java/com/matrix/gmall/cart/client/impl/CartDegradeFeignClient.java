package com.matrix.gmall.cart.client.impl;

import com.matrix.gmall.cart.client.CartFeignClient;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.cart.CartInfo;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        return null;
    }

    @Override
    public Result loadCartCache(String userId) {
        return null;
    }
}
