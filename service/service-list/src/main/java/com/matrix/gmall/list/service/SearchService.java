package com.matrix.gmall.list.service;

/**
 * 因为是存放到ES中 所以用不到Mapper
 *
 * @Author: yihaosun
 * @Date: 2021/10/11 16:22
 */
public interface SearchService {
    /**
     * 商品上架
     *
     * @param skuId skuId
     */
    void upperGoods(Long skuId);

    /**
     * 商品下架
     *
     * @param skuId skuId
     */
    void lowerGoods(Long skuId);

    /**
     * 更新热度排名
     * 在service-item上面调用 需要将热度排名的接口发布到feign上 service-list-client
     * @param skuId skuId
     */
    void incrHotScore(Long skuId);
}
