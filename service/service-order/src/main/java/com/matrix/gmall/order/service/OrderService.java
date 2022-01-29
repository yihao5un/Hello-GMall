package com.matrix.gmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.matrix.gmall.model.enums.ProcessStatus;
import com.matrix.gmall.model.order.OrderInfo;

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

    // ============== 以下是做回退之后不让再刷新的功能 ==============//

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
     * @param orderId orderId
     */
    void execExpiredOrder(Long orderId);

    /**
     * 根据订单Id 更新订单状态，订单进度状态
     * @param orderId orderId
     * @param processStatus processStatus
     */
    void updateOrderStatus(Long orderId, ProcessStatus processStatus);
}
