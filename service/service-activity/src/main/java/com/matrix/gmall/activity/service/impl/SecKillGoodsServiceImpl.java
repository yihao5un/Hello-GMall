package com.matrix.gmall.activity.service.impl;

import com.matrix.gmall.activity.service.SecKillGoodsService;
import com.matrix.gmall.activity.util.CacheHelper;
import com.matrix.gmall.common.constant.RedisConst;
import com.matrix.gmall.common.util.MD5;
import com.matrix.gmall.model.activity.OrderRecode;
import com.matrix.gmall.model.activity.SeckillGoods;
import jodd.io.StreamUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yihaosun
 * @date 2022/2/17 19:40
 */
@Service
public class SecKillGoodsServiceImpl implements SecKillGoodsService {
    /**
     * 直接从缓存查数据即可
     */
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> findAll() {
        // hvals
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).values();
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods findSeckillGoodsById(Long skuId) {
        // hget(key, field);
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(RedisConst.SECKILL_GOODS).get(skuId.toString());
        return seckillGoods;
    }

    @Override
    public void seckillOrder(Long skuId, String userId) {
        String state = (String) CacheHelper.get(skuId.toString());
        if ("0".equals(state)) {
            // 售空
            return;
        }
        // 判断用户是否下过单
        String userOrderkey = RedisConst.SECKILL_USER + userId;
        // 执行setnx命令
        Boolean flag = redisTemplate.opsForValue()
                .setIfAbsent(userOrderkey, skuId, RedisConst.SECKILL__TIMEOUT, TimeUnit.SECONDS);
        if (!flag) {
            // 用户已经下过订单
            return;
        }
        String seckillKey = RedisConst.SECKILL_STOCK_PREFIX + skuId;
        // 从缓存中减少库存数(监听的时候是leftPush())
        String skuIdValues = (String) redisTemplate.boundListOps(seckillKey).rightPop();
        if (StringUtils.isEmpty(skuIdValues)) {
            // 已经售空 并通知其他节点
            redisTemplate.convertAndSend("seckillpush", skuId + ":0");
            return;
        }
        // 将验证之后的对象存在缓存中 说明用户有资格下单了
        OrderRecode orderRecode = new OrderRecode();
        orderRecode.setUserId(userId);
        orderRecode.setSeckillGoods(findSeckillGoodsById(skuId));
        orderRecode.setNum(1);
        orderRecode.setOrderStr(MD5.encrypt(skuId + userId));
        // 放入缓存
        String orderKey = RedisConst.SECKILL_ORDERS;
        redisTemplate.boundHashOps(orderKey).put(userId, orderRecode);
        // 更新库存
        this.updateStockCount(skuId);
    }

    private void updateStockCount(Long skuId) {

    }
}
