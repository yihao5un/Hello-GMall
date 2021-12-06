package com.matrix.gmall.order.service;

import com.matrix.gmall.model.order.OrderDetail;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.mapper.OrderDetailMapper;
import com.matrix.gmall.order.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/12/6 22:31
 */
@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 加事务注解 因为是多表的
     *
     * @param orderInfo  orderInfo
     * @return 订单Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrderInfo(OrderInfo orderInfo) {
        orderInfoMapper.insert(orderInfo);
        // 获取到订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {

            orderDetailMapper.insert(orderDetail);
        }
        // 返回订单Id
        return orderInfo.getId();
    }
}
