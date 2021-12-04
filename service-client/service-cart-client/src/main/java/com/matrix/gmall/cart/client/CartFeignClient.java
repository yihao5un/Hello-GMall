package com.matrix.gmall.cart.client;

import com.matrix.gmall.cart.client.impl.CartDegradeFeignClient;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 数据接口发送到Feign上面
 *
 * @Author: yihaosun
 * @Date: 2021/11/13 19:49
 */
@FeignClient(value = "service-cart", fallback = CartDegradeFeignClient.class)
public interface CartFeignClient {

    /**
     * 添加购物车
     * 注意: 不需要 HttpServletRequest  因为在Feign传递的时候 拦截器中处理了
     *
     * @param skuId skuId
     * @param skuNum skuNum
     * @return Result
     */
    @PostMapping("/api/cart/addToCart/{skuId}/{skuNum}")
    Result addToCart(@PathVariable("skuId") Long skuId,
                     @PathVariable("skuNum") Integer skuNum);

    /**
     * 根据用户Id 查询购物车列表
     * @param userId userId
     * @return Result
     */
    @GetMapping("/api/cart/getCartCheckedList/{userId}")
    List<CartInfo> getCartCheckedList(@PathVariable("userId") String userId);

    /**
     * 根据userId 查询购物车最新价格
     *
     * @param userId userId
     * @return Result
     */
    @GetMapping("/api/cart/loadCartCache/{userId}")
    Result loadCartCache(@PathVariable("userId") String userId);
}
