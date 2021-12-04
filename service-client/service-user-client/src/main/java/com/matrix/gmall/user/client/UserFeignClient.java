package com.matrix.gmall.user.client;

import com.matrix.gmall.model.cart.CartInfo;
import com.matrix.gmall.model.user.UserAddress;
import com.matrix.gmall.user.client.impl.UserDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/11/24 20:52
 */
@FeignClient(value = "service-user", fallback = UserDegradeFeignClient.class)
public interface UserFeignClient {
    /**
     * 发布数据接口给别人调用
     * /api/user/inner/findUserAddressListByUserId/{userId} 这个地址就是 service-user 中对应的地址
     *
     * @param userId userId
     * @return List<UserAddress> List<UserAddress>
     */
    @GetMapping("/api/user/inner/findUserAddressListByUserId/{userId}")
    List<UserAddress> findUserAddressListByUserId(@PathVariable String userId);

    /**
     * 根据用户Id查询购物车列表
     * @param userId userId
     * @return List<CartInfo>
     */
    @GetMapping("/api/cart/getCartCheckedList/{userId}")
    List<CartInfo> getCartCheckedList(@PathVariable("userId") String userId);
}
