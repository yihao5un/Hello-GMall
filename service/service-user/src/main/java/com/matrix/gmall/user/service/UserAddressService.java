package com.matrix.gmall.user.service;

import com.matrix.gmall.model.user.UserAddress;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/11/24 20:37
 */
public interface UserAddressService {
    /**
     * 根据用户Id查询收货地址列表
     *
     * @param userId userId
     * @return List<UserAddress> List<UserAddress>
     */
    List<UserAddress> findUserAddressListByUserId(String userId);
}
