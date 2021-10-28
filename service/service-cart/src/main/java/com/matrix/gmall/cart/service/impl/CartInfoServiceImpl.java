package com.matrix.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.gmall.cart.mapper.CartInfoMapper;
import com.matrix.gmall.cart.service.CartInfoService;
import com.matrix.gmall.model.cart.CartInfo;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author: yihaosun
 * @Date: 2021/10/28 21:02
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        // 根据userId 和 skuId 能够确定唯一的商品
        CartInfo cartInfoExit = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, userId)
                .eq(CartInfo::getSkuId, skuId));
        // 判断数据是否为空 如果为空的话 那么插入这条数据
        if (cartInfoExit != null) {
            // 数量相加
            cartInfoExit.setSkuNum(cartInfoExit.getSkuNum() + skuNum);
            // 赋值商品的实时价格 注意在数据库中是不存在的 需要去查一下
            cartInfoExit.setSkuPrice(productFeignClient.getSkuPrice(skuId));
            // 执行更新语句
            cartInfoMapper.updateById(cartInfoExit);

            // TODO 缓存
        } else {
            // 为了查询到 skuName price 和 img 等信息
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            CartInfo cartInfo = CartInfo
                    .builder()
                    .skuId(skuId)
                    .skuName(skuInfo.getSkuName())
                    .skuNum(skuNum)
                    .userId(userId)
                    .cartPrice(skuInfo.getPrice())
                    .imgUrl(skuInfo.getSkuDefaultImg())
                    .createTime(new Timestamp(System.currentTimeMillis()))
                    .updateTime(new Timestamp(System.currentTimeMillis()))
                    .build();
            cartInfoMapper.insert(cartInfo);

            // TODO 缓存

        }
        // 整个key 数据类型 用哈希 redisTemplate
        // TODO 什么样的Redis的数据类型用Hash
    }
}
