package com.matrix.gmall.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.cart.client.CartFeignClient;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.util.AuthContextHolder;
import com.matrix.gmall.model.cart.CartInfo;
import com.matrix.gmall.model.order.OrderDetail;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.model.user.UserAddress;
import com.matrix.gmall.order.service.OrderService;
import com.matrix.gmall.product.client.ProductFeignClient;
import com.matrix.gmall.user.client.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

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
        String tradeNo = orderService.getTradeNo(userId);
        map.put("tradeNo", tradeNo);
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
        String tradeNo = request.getParameter("tradeNo");
        Boolean result = orderService.checkTradeNo(tradeNo, userId);
        if (Boolean.FALSE.equals(result)) {
            // 比较失败
            return Result.fail().message("不能无刷新回退提交订单");
        }
        orderService.deleteTradeNo(userId);

        // 可以使用多线程
        List<CompletableFuture> futureList = new ArrayList<>();
        // 存储错误信息集合
        List<String> errorList = new ArrayList<>();
        // 远程调用
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        for (OrderDetail orderDetail : orderDetailList) {
            //1. 开线程 CompletableFuture runAsync无返回
            CompletableFuture<Void> stockCompletableFuture = CompletableFuture.runAsync(() -> {
                boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
                if (!flag) {
//                    return Result.fail().message(orderDetail.getSkuName() + "库存不足");
                    errorList.add(orderDetail.getSkuName() + "库存不足");
                }
            }, threadPoolExecutor);
            // 添加到异步编排集合
            futureList.add(stockCompletableFuture);

            //2. 开线程 CompletableFuture runAsync无返回
            CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
            BigDecimal orderPrice = orderDetail.getOrderPrice();
            BigDecimal skuPrice = productFeignClient.getSkuPrice(orderDetail.getSkuId());
            if (orderPrice.compareTo(skuPrice) != 0) {
                // 价格有变动设置最新的价格
                cartFeignClient.loadCartCache(userId);
//                return Result.fail().message(orderDetail.getSkuName() + "价格有变动");
                errorList.add(orderDetail.getSkuName() + "价格有变动");
            }}, threadPoolExecutor);
            // 添加到异步编排集合
            futureList.add(priceCompletableFuture);
        }

        // 所有数据结果: errorList futureList 执行异步编排的集合
        // 多任务组合
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
        // 判断errorList中是否有数据 如果有数据 不可以跑了
        if (CollectionUtils.isEmpty(errorList)) {
            return Result.fail().message(StringUtils.join(errorList, ","));
        }

//        for (OrderDetail orderDetail : orderDetailList) {
//            boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
//            if (!flag) {
//                return Result.fail().message(orderDetail.getSkuName() + "库存不足");
//            }
//            // 比较订单价格与实时价格
//            BigDecimal orderPrice = orderDetail.getOrderPrice();
//            BigDecimal skuPrice = productFeignClient.getSkuPrice(orderDetail.getSkuId());
//            if (orderPrice.compareTo(skuPrice) != 0) {
//                // 价格有变动设置最新的价格
//                cartFeignClient.loadCartCache(userId);
//                return Result.fail().message(orderDetail.getSkuName() + "价格有变动");
//            }
//        }

        orderInfo.setUserId(Long.parseLong(userId));
        Long orderId = orderService.saveOrderInfo(orderInfo);
        return Result.ok(orderId);
    }

    @GetMapping("inner/getOrderInfo/{orderId}")
    public OrderInfo getOrderInfo(@PathVariable("orderId") Long orderId) {
        return orderService.getOrderInfo(orderId);
    }

    @PostMapping("orderSplit")
    public String orderSplit(HttpServletRequest request) {
        String orderId = request.getParameter("orderId");
        String wareSkuMap = request.getParameter("wareSkuMap");
        List<OrderInfo> orderInfoList = orderService.orderSplit(orderId, wareSkuMap);
        List<Map<String, Object>> orderInfoListMap = new ArrayList<>();
        orderInfoList.forEach(orderInfo -> {
            Map<String, Object> map = orderService.initWareOrder(orderInfo);
            orderInfoListMap.add(map);
        });
        // 返回数据
        return JSON.toJSONString(orderInfoListMap);
    }

    @PostMapping("inner/seckill/submitOrder")
    public Long submitOrder(@RequestBody OrderInfo orderInfo) {
        return orderService.saveOrderInfo(orderInfo);
    }
}
