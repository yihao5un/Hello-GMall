package com.matrix.gmall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.common.service.RabbitService;
import com.matrix.gmall.model.enums.PaymentStatus;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.model.payment.PaymentInfo;
import com.matrix.gmall.payment.service.PaymentService;
import com.matrix.gmall.payment.mapper.PaymentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author yihaosun
 * @date 2022/2/7 17:05
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        // 查询是否有支付记录
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOrderId, orderInfo.getId());
        queryWrapper.eq(PaymentInfo::getPaymentType, paymentType);
        Integer count = paymentInfoMapper.selectCount(queryWrapper);
        // 如果有记录直接返回即可
        if (0 != count) { return; }
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setSubject(orderInfo.getTradeBody());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfoMapper.insert(paymentInfo);
    }

    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo, String name) {
        return paymentInfoMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOutTradeNo, outTradeNo)
                .eq(PaymentInfo::getPaymentType, name));
    }

    @Override
    public void paySuccess(String outTradeNo, String name, Map<String, String> paramMap) {
        PaymentInfo payment = getPaymentInfo(outTradeNo, name);
        //  判断状态如果是CLOSED或者PAID的话直接返回即可 不必发送队列
        if(PaymentStatus.CLOSED.name().equals(payment.getPaymentStatus())
                || PaymentStatus.PAID.name().equals(payment.getPaymentStatus())){
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setTradeNo(paramMap.get("trade_no"));
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        paymentInfoMapper.update(paymentInfo, new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOutTradeNo, outTradeNo)
                .eq(PaymentInfo::getPaymentType, name));
//      this.updatePaymentInfo(outTradeNo, name, paymentInfo);
//      MQ向order模块发送消息 消息体发送OrderId即可 然后在Order模块根据OrderId去更新状态(在receiver包里面)
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_PAY, payment.getOrderId());
    }

    @Override
    public void updatePaymentInfo(String outTradeNo, String name, PaymentInfo paymentInfo) {
        paymentInfoMapper.update(paymentInfo, new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOutTradeNo, outTradeNo)
                .eq(PaymentInfo::getPaymentType, name));
    }

    @Override
    public void closePaymentInfo(Long orderId) {
        // 在生成二维码的时候要才会有PaymentInfo的记录 先判断一下时候有记录
        Integer count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, orderId));
        if (count.equals(0)) {
            // 直接返回即可
            return;
        }

        // 更新
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatus.CLOSED.name());
        paymentInfoMapper.update(paymentInfo, new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderId, orderId));
    }
}
