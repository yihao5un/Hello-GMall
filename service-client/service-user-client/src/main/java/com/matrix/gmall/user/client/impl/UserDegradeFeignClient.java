package com.matrix.gmall.user.client.impl;

import com.matrix.gmall.model.cart.CartInfo;
import com.matrix.gmall.model.user.UserAddress;
import com.matrix.gmall.user.client.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/11/24 20:54
 */
@Component
public class UserDegradeFeignClient implements UserFeignClient {
    @Override
    public List<UserAddress> findUserAddressListByUserId(String userId) {
        return null;
    }

    @Override
    public List<CartInfo> getCartCheckedList(String userId) {
        return null;
    }
}
