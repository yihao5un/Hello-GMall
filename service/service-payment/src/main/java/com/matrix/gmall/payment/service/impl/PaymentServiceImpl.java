package com.matrix.gmall.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.gmall.model.enums.PaymentStatus;
import com.matrix.gmall.model.order.OrderInfo;
import com.matrix.gmall.model.payment.PaymentInfo;
import com.matrix.gmall.payment.service.PaymentService;
import com.matrix.gmall.payment.mapper.PaymentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author yihaosun
 * @date 2022/2/7 17:05
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

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
}
