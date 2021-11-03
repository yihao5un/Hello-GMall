package com.matrix.gmall.cart.service;

import com.matrix.gmall.model.cart.CartInfo;

/**
 * 同步 Redis 异步操作 MySQL
 *
 * @Author: yihaosun
 * @Date: 2021/11/1 15:35
 */
public interface CartAsyncService {
    /**
     * 新增
     * @param cartInfo cartInfo
     */
    void saveCartInfo(CartInfo cartInfo);

    /**
     * 更新
     * @param cartInfo cartInfo
     */
    void updateCartInfo(CartInfo cartInfo);
}
