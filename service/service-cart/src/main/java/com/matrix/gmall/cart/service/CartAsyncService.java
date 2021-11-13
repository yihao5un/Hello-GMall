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

    /**
     * 删除
     * @param userId userId
     */
    void delCartInfo(String userId);

    /**
     * 选中变更状态
     * @param userId userId
     * @param isChecked isChecked
     * @param skuId skuId
     */
    void checkCart(String userId, Integer isChecked, Long skuId);

    /**
     * 删除购物车
     *
     * @param skuId skuId
     * @param userId userId
     */
    void deleteCart(Long skuId, String userId);
}
