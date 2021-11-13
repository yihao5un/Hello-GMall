package com.matrix.gmall.cart.controller;

import com.matrix.gmall.cart.service.CartInfoService;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.util.AuthContextHolder;
import com.matrix.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/11/1 11:17
 */
@RestController
@RequestMapping("api/cart")
public class CaratApiController {
    @Autowired
    private CartInfoService cartInfoService;

    @PostMapping("addToCart/{skuId}/{skuNum}")
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

    @GetMapping("cartList")
    public Result<List<CartInfo>> cartList(HttpServletRequest request) {
        // 获取用户Id 在网关已经获取到了 在请求头里面
        // 需要有HttpServletRequest
        String userId = AuthContextHolder.getUserId(request);
        // 未登陆 没有用户Id, 给一个临时的用户Id
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartList = cartInfoService.getCartList(userId, userTempId);
        return Result.ok(cartList);
    }

    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result<String> checkCart(@PathVariable Long skuId,
                                    @PathVariable Integer isChecked, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartInfoService.checkCart(userId, isChecked, skuId);
        return Result.ok();
    }

    @DeleteMapping("deleteCart/{skuId}")
    public Result<String> deleteCart(@PathVariable("skuId") Long skuId,
                                     HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            userId = AuthContextHolder.getUserTempId(request);
        }
        cartInfoService.deleteCart(skuId, userId);
        return Result.ok();
    }
}
