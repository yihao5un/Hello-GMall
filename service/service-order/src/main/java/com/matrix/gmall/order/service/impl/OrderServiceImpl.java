package com.matrix.gmall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.common.service.RabbitService;
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
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: yihaosun
 * @Date: 2021/12/6 22:31
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitService rabbitService;

    @Value("${ware.url}")
    private String wareUrl;

    /**
     * 加事务注解 因为是多表的
     *
     * @param orderInfo orderInfo
     * @return 订单Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrderInfo(OrderInfo orderInfo) {
        // 总金额：单价*数量
        orderInfo.sumTotalAmount();
        // 自动赋值
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        // 在实现类中能否获取到userId? 在控制器获取！
        // 第三方交易编号要求唯一！
        String outTradeNo = "MATRIX" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        // 订单描述
        orderInfo.setTradeBody("ORDER_DESC");
        // 可以将送货清单中的skuName 进行拼接！放入TradeBody 中！
        orderInfo.setCreateTime(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());
        // 默认24小时过期！
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        orderInfoMapper.insert(orderInfo);
        // 获取到订单明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insert(orderDetail);
        }
        Long orderId = orderInfo.getId();
        // 在生成订单的时候, 消息队列发送取消订单的消息时间是@see MqConst.DELAY_TIME, 发的内容是: 订单Id
        // 监听消息在: @link com.matrix.gmall.mq.receiver.
        rabbitService.sendDelayMessage(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL, MqConst.ROUTING_ORDER_CANCEL, orderId, MqConst.DELAY_TIME);
        //  返回订单Id
        return orderId;
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

    @Override
    public void execExpiredOrder(Long orderId) {
//        OrderInfo orderInfo = new OrderInfo();
//        // 需要给orderInfo设置orderId之后才能使用 updateById这个方法
//        orderInfo.setId(orderId);
//        orderInfo.setOrderStatus(OrderStatus.CLOSED.name());
//        orderInfo.setProcessStatus(ProcessStatus.CLOSED.name());
//        orderInfoMapper.updateById(orderInfo);
        // TODO 后续需要订单状态和订单进度状态的更新 {@link #updateOrderStatus(Long orderId, ProcessStatus processStatus)} 用一个方法就可以更新两个状态
        updateOrderStatus(orderId, ProcessStatus.CLOSED);
    }

    @Override
    public void updateOrderStatus(Long orderId, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        // 从订单进度状态中获取订单状态 没有set的东西是不会改变的
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setProcessStatus(processStatus.name());
        orderInfoMapper.updateById(orderInfo);
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        // TODO 后续可能会用到订单明细 因此不用IService的getById()
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, orderId));
        if (!CollectionUtils.isEmpty(orderDetails)) {
            orderInfo.setOrderDetailList(orderDetails);
        }
        return orderInfo;
    }

    @Override
    public void sendOrderStatus(Long orderId) {
        // 更改订单状态
        this.updateOrderStatus(orderId, ProcessStatus.NOTIFIED_WARE);
        // 发送内容
        String wareJson = initWareOrder(orderId);
        // 发送消息
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_WARE_STOCK, MqConst.ROUTING_WARE_STOCK, wareJson);
    }

    /**
     * 发送减库存消息的字符串
     *
     * @param orderId orderId
     * @return String
     */
    private String initWareOrder(Long orderId) {
        // 发送的字符串是由实体类总的部分字段组成的!
        OrderInfo orderInfo = this.getOrderInfo(orderId);
        // orderInfo 组成 Map
        Map<String, Object> map = initWareOrder(orderInfo);
        return JSON.toJSONString(map);
    }

    /**
     * 将OrderInfo的部分字段转换为Map集合
     *
     * @param orderInfo orderInfo
     * @return Map<String, Object>
     */
    private Map<String, Object> initWareOrder(OrderInfo orderInfo) {
        Map<String, Object> hashMap = new HashMap<>(16);
        hashMap.put("orderId", orderInfo.getId());
        hashMap.put("consignee", orderInfo.getConsignee());
        hashMap.put("consigneeTel", orderInfo.getConsigneeTel());
        hashMap.put("orderComment", orderInfo.getOrderComment());
        hashMap.put("orderBody", orderInfo.getTradeBody());
        hashMap.put("deliveryAddress", orderInfo.getDeliveryAddress());
        hashMap.put("paymentWay", "2");
        // 拆单的时候使用
        hashMap.put("wareId", orderInfo.getWareId());

        List<Map<String, Object>> detailMapList = new ArrayList<>();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        orderDetailList.forEach(orderDetail -> {
            Map<String, Object> detailMap = new HashMap<>(16);
            detailMap.put("skuId", orderDetail.getSkuId());
            detailMap.put("skuNum", orderDetail.getSkuNum());
            detailMap.put("skuName", orderDetail.getSkuName());
            detailMapList.add(detailMap);
        });
        return hashMap;
    }
}
