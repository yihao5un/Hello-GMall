package com.matrix.gmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.matrix.gmall.model.enums.ProcessStatus;
import com.matrix.gmall.model.order.OrderInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/12/6 22:28
 */
public interface OrderService extends IService<OrderInfo> {
    /**
     * 保存订单
     *
     * @param orderInfo  orderInfo
     * @return 返回的订单号
     */
    Long saveOrderInfo(OrderInfo orderInfo);

    //============== 以下是做回退之后不让再刷新的功能 ==============//

    /**
     * 获取交易号
     *
     * @param userId 用户Id
     * @return
     */
    String getTradeNo(String userId);

    /**
     * 比较流水号
     *
     * @param tradeNo tradeNo
     * @param userId userId
     * @return
     */
    Boolean checkTradeNo(String tradeNo, String userId);

    /**
     * 删除交易号
     *
     * @param userId userId
     */
    void deleteTradeNo(String userId);

    /**
     * 验证库存
     *
     * @param skuId skuId
     * @param skuNum skuNum
     * @return boolean
     */
    boolean checkStock(Long skuId, Integer skuNum);

    /**
     * 修改订单状态
     *
     * @param orderId orderId
     */
    void execExpiredOrder(Long orderId);

    /**
     * 根据订单Id 更新订单状态，订单进度状态
     *
     * @param orderId orderId
     * @param processStatus processStatus
     */
    void updateOrderStatus(Long orderId, ProcessStatus processStatus);

    /**
     * 根据订单Id 查询订单信息
     * 要将接口发布到Feign上用于WebAll的整合做页面渲染
     * 要在控制器中添加接口
     *
     * @param orderId orderId
     * @return OrderInfo
     */
    OrderInfo getOrderInfo(Long orderId);

    /**
     * 发送消息给库存
     *
     * @param orderId orderId
     */
    void sendOrderStatus(Long orderId);

    /**
     * 将OrderInfo数据变成Map集合
     *
     * @param orderInfo orderInfo
     * @return Map<String, Object>
     */
    Map<String, Object> initWareOrder(OrderInfo orderInfo);

    /**
     * 拆单方法(仓库不同的时候)
     *
     * @param orderId orderId
     * @param wareSkuMap wareSkuMap
     * @return List<OrderInfo>
     */
    List<OrderInfo> orderSplit(String orderId, String wareSkuMap);
}
