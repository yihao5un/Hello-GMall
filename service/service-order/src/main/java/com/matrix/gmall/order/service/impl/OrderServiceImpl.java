package com.matrix.gmall.order.service.impl;

import com.matrix.gmall.common.util.HttpClientUtil;
import com.matrix.gmall.model.enums.OrderStatus;
import com.matrix.gmall.model.enums.ProcessStatus;
import com.matrix.gmall.model.order.OrderDetail;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.order.mapper.OrderDetailMapper;
import com.matrix.gmall.order.mapper.OrderInfoMapper;
import com.matrix.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: yihaosun
 * @Date: 2021/12/6 22:31
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${ware.url}")
    private String wareUrl;

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
            // 自动赋值
            orderInfo.sumTotalAmount();
            orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
            // userId只能在控制器中获取
            String outTradeNo = "MATRIX" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setTradeBody("Matrix");
            orderInfo.setCreateTime(new Date());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            // 设置过期时间 默认为1天
            orderInfo.setExpireTime(calendar.getTime());
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
            orderDetailMapper.insert(orderDetail);
            // 获取订单明细
            for (OrderDetail detail : orderDetailList) {
                detail.setOrderId(orderInfo.getId());
                orderDetailMapper.insert(detail);
            }
        }
        // 返回订单Id
        return orderInfo.getId();
    }

    @Override
    public String getTradeNo(String userId) {
        // 制作流水号
        String tradeNo = UUID.randomUUID().toString();
        // 存储袋缓存中
        String tradeNoKey = "user" + userId + ":tradeCode";
        redisTemplate.opsForValue().set(tradeNoKey, tradeNo);
        return tradeNo;
    }

    @Override
    public Boolean checkTradeNo(String tradeNo, String userId) {
        String tradeNoKey = "user" + userId + ":tradeCode";
        String tradeNoRedis = (String) redisTemplate.opsForValue().get(tradeNoKey);
        return tradeNo.equals(tradeNoRedis);
    }

    @Override
    public void deleteTradeNo(String userId) {
        String tradeNoKey = "user" + userId + ":tradeCode";
        redisTemplate.delete(tradeNoKey);
    }

    @Override
    public boolean checkStock(Long skuId, Integer skuNum) {
        // 远程调用库存
        String result = HttpClientUtil.doGet(wareUrl + "/hasStock?skuId=" + skuId + "&num" + skuNum);
        // 0:没有库存 1:有库存
        return "1".equals(result);
    }
}
