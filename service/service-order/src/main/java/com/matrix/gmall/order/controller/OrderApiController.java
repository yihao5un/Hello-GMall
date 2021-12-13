package com.matrix.gmall.order.controller;

import com.matrix.gmall.cart.client.CartFeignClient;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.util.AuthContextHolder;
import com.matrix.gmall.model.cart.CartInfo;
import com.matrix.gmall.model.order.OrderDetail;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.model.user.UserAddress;
import com.matrix.gmall.order.service.OrderService;
import com.matrix.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 这是一个远程调用的地址 供web-all使用的
 *
 * @Author: yihaosun
 * @Date: 2021/12/4 11:43
 */
@RestController
@RequestMapping("api/order")
public class OrderApiController {
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private CartFeignClient cartFeignClient;

    @Autowired
    private OrderService orderService;

    /**
     * 确认订单 (带有auth的必须进行登陆)
     */
    @GetMapping("auth/trade")
    public Result trade(HttpServletRequest request) {
        // 获取用户Id
        String userId = AuthContextHolder.getUserId(request);
        Map<String, Object> map = new HashMap<>();
        // 远程调用才能获取到收获地址列表
        List<UserAddress> userAddressList = userFeignClient.findUserAddressListByUserId(userId);
        // 获取送货清单
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        // 前端获取到这个map 这个key就是前端要获取的数据 value就是值
        // cartCheckedList 集合数据赋值给订单明细集合
        List<OrderDetail> detailArrayList = new ArrayList<>();
        for (CartInfo cartInfo : cartCheckedList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setCreateTime(new Date());
            detailArrayList.add(orderDetail);
        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(detailArrayList);
        orderInfo.sumTotalAmount();
        map.put("totalAmount", orderInfo.getTotalAmount());
        map.put("totalNum", detailArrayList.size());
        map.put("detailArrayList", detailArrayList);
        map.put("userAddressList", userAddressList);
        return Result.ok(map);
    }

    /**
     * 保存订单的控制器
     * 前端页面传递的是Json 数据 ，后台使用@RequestBody接收
     *
     * @param orderInfo orderInfo
     * @return orderId
     */
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        orderInfo.setUserId(Long.parseLong(userId));
        Long orderId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);
    }
}
