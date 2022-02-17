package com.matrix.gmall.activity.service.impl;

import com.matrix.gmall.activity.service.SecKillGoodsService;
import com.matrix.gmall.common.constant.RedisConst;
import com.matrix.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
