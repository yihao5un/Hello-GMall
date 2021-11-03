package com.matrix.gmall.cart.service.impl;

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
        // TODO 代码有问题 存在Id为NULL的情况
        cartInfoMapper.updateById(cartInfo);
    }
}
