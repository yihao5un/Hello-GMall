package com.matrix.gmall.cart.client;

import com.matrix.gmall.cart.client.impl.CartDegradeFeignClient;
import com.matrix.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

}
