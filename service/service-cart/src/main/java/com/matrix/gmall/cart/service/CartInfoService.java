package com.matrix.gmall.cart.service;

/**
 * @Author: yihaosun
 * @Date: 2021/10/28 21:00
 */
public interface CartInfoService {
    /**
     * 添加购物车的方法
     * @param skuId skuId
     * @param userId userId
     * @param skuNum skuNum
     */
    void addToCart(Long skuId, String userId, Integer skuNum);
}
