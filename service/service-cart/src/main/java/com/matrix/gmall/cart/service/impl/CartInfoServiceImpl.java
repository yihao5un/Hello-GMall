package com.matrix.gmall.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.gmall.cart.mapper.CartInfoMapper;
import com.matrix.gmall.cart.service.CartAsyncService;
import com.matrix.gmall.cart.service.CartInfoService;
import com.matrix.gmall.common.constant.RedisConst;
import com.matrix.gmall.model.cart.CartInfo;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yihaosun
 * @Date: 2021/10/28 21:02
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private CartAsyncService cartAsyncService;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(Long skuId, String userId, Integer skuNum) {
        CartInfo cartInfoExit = null;
        String cartKey = null;
        try {
            // 根据userId 和 skuId 能够确定唯一的商品
            cartInfoExit = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                    .eq(CartInfo::getUserId, userId)
                    .eq(CartInfo::getSkuId, skuId));

            // 添加缓存
            // 获取到购物车的Key
            cartKey = getCartKey(userId);

            // 判断数据是否为空 如果为空的话 那么插入这条数据
            if (cartInfoExit != null) {
                // 数量相加
                cartInfoExit.setSkuNum(cartInfoExit.getSkuNum() + skuNum);
                // 赋值商品的实时价格 注意在数据库中是不存在的 需要去查一下
                cartInfoExit.setSkuPrice(productFeignClient.getSkuPrice(skuId));
                // 更新时间
                cartInfoExit.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                // 执行更新语句
                // cartInfoMapper.updateById(cartInfoExit);
                cartAsyncService.updateCartInfo(cartInfoExit);
                // TODO 缓存
                // redisTemplate.opsForHash().put(cartKey, skuId.toString(),cartInfoExit);
            } else {
                // 为了查询到 skuName price 和 img 等信息
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                CartInfo cartInfo = CartInfo
                        .builder()
                        .skuId(skuId)
                        .skuName(skuInfo.getSkuName())
                        .skuPrice(skuInfo.getPrice())
                        .skuNum(skuNum)
                        .userId(userId)
                        .imgUrl(skuInfo.getSkuDefaultImg())
                        .createTime(new Timestamp(System.currentTimeMillis()))
                        .updateTime(new Timestamp(System.currentTimeMillis()))
                        .build();
                //  cartInfoMapper.insert(cartInfo);
                cartAsyncService.saveCartInfo(cartInfo);
                // 为了省去原来的两行代码
                cartInfoExit = cartInfo;
                // TODO 添加到数据库的同时添加到缓存
                // redisTemplate.opsForHash().put(cartKey, skuId.toString(),cartInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Objects.nonNull(cartInfoExit)) {
            // 添加购物车到缓存
            redisTemplate.opsForHash().put(cartKey, skuId.toString(), cartInfoExit);
            // 购物车 添加到了MySQL Redis !
            this.setCartKeyExpire(cartKey);
        }
    }

    // 获取购物车的Key
    private String getCartKey(String userId) {
        // 整个key 数据类型 用哈希 redisTemplate
        // TODO 什么样的Redis的数据类型用Hash  hset (key, field, value)  hget (key, field)
        // 查看用户的购物车 所以放入的是userId
        String cartKey = RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        // key -> cartKey, field -> skuId, value -> cartInfo
        return cartKey;
    }

    // 给当前购物车设置一个过期的时间
    private void setCartKeyExpire(String cartKey) {
        redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }
}
