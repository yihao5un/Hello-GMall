package com.matrix.gmall.cart.controller;

import com.matrix.gmall.cart.service.CartInfoService;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.util.AuthContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yihaosun
 * @Date: 2021/11/1 11:17
 */
@RestController
@RequestMapping("api/cart")
public class CaratApiController {
    @Autowired
    private CartInfoService cartInfoService;

    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,
                            @PathVariable Integer skuNum,
                            HttpServletRequest request) {
        // 获取用户Id 在网关已经获取到了 在请求头里面
        // 需要有HttpServletRequest
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            // 未登陆 没有用户Id, 给一个临时的用户Id
            userId = AuthContextHolder.getUserTempId(request);

        }
        cartInfoService.addToCart(skuId, userId, skuNum);
        return Result.ok();
    }
}
