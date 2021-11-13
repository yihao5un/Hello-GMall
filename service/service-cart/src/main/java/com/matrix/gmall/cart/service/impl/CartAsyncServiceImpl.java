package com.matrix.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.matrix.gmall.cart.mapper.CartInfoMapper;
import com.matrix.gmall.cart.service.CartAsyncService;
import com.matrix.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 同步Redis 异步MySQL
 *
 * @Author: yihaosun
 * @Date: 2021/11/1 15:38
 */
@Service
public class CartAsyncServiceImpl implements CartAsyncService {
    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Override
    @Async
    public void saveCartInfo(CartInfo cartInfo) {
        cartInfoMapper.insert(cartInfo);
    }

    @Override
    @Async
    public void updateCartInfo(CartInfo cartInfo) {
        // TODO 代码有问题 存在Id为NULL的情况 需要构建更新条件
        cartInfoMapper.update(cartInfo, new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getSkuId, cartInfo.getSkuId())
                .eq(CartInfo::getUserId, cartInfo.getUserId()));
    }

    @Override
    @Async
    public void delCartInfo(String userId) {
        cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, userId));
    }

    @Override
    @Async
    public void checkCart(String userId, Integer isChecked, Long skuId) {
        CartInfo cartInfo = CartInfo.builder().isChecked(isChecked).build();
        cartInfoMapper.update(cartInfo, new UpdateWrapper<CartInfo>()
                .eq("user_id", userId)
                .eq("sku_id", skuId));
        // .set("is_check", isChecked)
        // updateWrapper 里面有 set 直接给字段名 和 字段值就可以了
    }

    @Override
    @Async
    public void deleteCart(Long skuId, String userId) {
        cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, userId)
                .eq(CartInfo::getSkuId, skuId));
    }
}
