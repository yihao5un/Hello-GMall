package com.matrix.gmall.activity.service;

import com.matrix.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @author yihaosun
 * @date 2022/2/17 19:37
 */
public interface SecKillGoodsService {

    /**
     * 秒杀商品列表
     *
     * @return List<SeckillGoods>
     */
    List<SeckillGoods> findAll();

    /**
     * 秒杀商品详情
     *
     * @param skuId skuId
     * @return SeckillGoods
     */
    SeckillGoods findSeckillGoodsById(Long skuId);

    /**
     * 监听消息 预下单
     *
     * @param skuId skuId
     * @param userId userId
     */
    void seckillOrder(Long skuId, String userId);
}
